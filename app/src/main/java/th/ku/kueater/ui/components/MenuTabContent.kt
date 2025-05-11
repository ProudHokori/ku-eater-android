package th.ku.kueater.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import th.ku.kueater.viewmodel.AuthViewModel
import th.ku.kueater.viewmodel.BookmarkViewModel
import th.ku.kueater.viewmodel.MenuViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val tanColor = Color(0xFFFDF5E6) // Light tan; adjust if needed
val titleColor = Color(0xFF00695C)

@Composable
fun MenuTabContent(
    menuViewModel: MenuViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel(),
) {
    val menus by menuViewModel.menus.collectAsState()
    val top10Menus by menuViewModel.top10Menus.collectAsState()
    val isLoading by menuViewModel.isLoading.collectAsState()
    val isLoadingTop10 by menuViewModel.isLoadingTop10.collectAsState()
    val error by menuViewModel.error.collectAsState()
    val userId = authViewModel.getUserId()

    LaunchedEffect(userId) {
        if (userId != null) {
            menuViewModel.setUserId(userId)
        }
    }

    LaunchedEffect(top10Menus) {
        val tag = "Top10MenusDebug"
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        Log.d(tag, "[$timestamp] Top10Menus changed → Size: ${top10Menus.size}")
        top10Menus.take(3).forEachIndexed { index, menu ->
            Log.d(tag, "   #$index → ID: ${menu.id}, Name: ${menu.like}")
        }
    }

    LaunchedEffect(menus) {
        val tag = "MenusDebug"
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        Log.d(tag, "[$timestamp] Menus changed → Size: ${menus.size}")
        menus.take(3).forEachIndexed { index, menu ->
            Log.d(tag, "   #$index → ID: ${menu.id}, Name: ${menu.like}")
        }
    }

    if (userId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp // 100vh equivalent
    val cardWidth = screenWidth * 0.5f
    val minHeight = 600.dp // Adjust this value as your minimum height requirement

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top 10 Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tanColor)
                    .padding(vertical = 2.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    text = "Top 10 most like Menu from the EATER!",
                    color = titleColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                when {
                    isLoadingTop10 -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        Text(
                            text = "Error loading top 10: $error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    top10Menus.isEmpty() -> {
                        Text(
                            text = "No top menus available",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else -> {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            items(top10Menus.size) { index ->
                                val menuItem = top10Menus[index]
                                Box(modifier = Modifier.width(cardWidth)) {
                                    MenuCard(
                                        item = menuItem,
                                        authViewModel = authViewModel,
                                        bookmarkViewModel = bookmarkViewModel,
                                        menuViewModel = menuViewModel,
                                        width = 200.dp, // Example width
                                        height = 325.dp // Example height
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Spacer between sections
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // All Menus Section with maxHeight and minHeight
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tanColor)
            ) {
                Text(
                    text = "All Menus from Bar-Mai",
                    color = titleColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = minHeight, max = screenHeight) // Min and max height constraints
                        .background(tanColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()) // Enable scrolling within the section
                            .padding(horizontal = 8.dp) // Reduced horizontal padding to 0.dp
                    ) {
                        // Manual grid layout for 2 columns
                        val rows = (menus.size + 1) / 2 // Ceiling division for rows
                        for (rowIndex in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp) // Reduced spacing between columns to 0.dp
                            ) {
                                for (columnIndex in 0 until 2) {
                                    val itemIndex = rowIndex * 2 + columnIndex
                                    if (itemIndex < menus.size) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(2.dp), // Reduced padding to 0.dp
                                            contentAlignment = Alignment.TopCenter
                                        ) {
                                            MenuCard(
                                                item = menus[itemIndex],
                                                authViewModel = authViewModel,
                                                bookmarkViewModel = bookmarkViewModel,
                                                menuViewModel = menuViewModel,
                                                width = 200.dp, // Example width
                                                height = 325.dp // Example height
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        // Trigger next page load when scrolled to bottom
                        if (menus.isNotEmpty() && !isLoading) {
                            LaunchedEffect(Unit) {
                                menuViewModel.loadNextPage()
                            }
                        }

                        // Loading indicator
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}