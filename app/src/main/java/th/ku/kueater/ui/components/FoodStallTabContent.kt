package th.ku.kueater.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import th.ku.kueater.viewmodel.AuthViewModel
import th.ku.kueater.viewmodel.StallViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FoodStallTabContent(
    viewModel: StallViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val stalls by viewModel.stalls.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userId = authViewModel.getUserId()

    // Fetch stalls when userId becomes available
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.setUserId(userId)
        }
    }

    if (userId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp // 100vh equivalent
    val minHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // All Stalls Section with maxHeight and minHeight
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tanColor)
            ) {
                Text(
                    text = "All Stalls from Bar-Mai",
                    color = titleColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp // Match the font size from MenuTabContent
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
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        when {
                            isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            stalls.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No food stalls found", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                            else -> {
                                // Single-column layout for stalls
                                stalls.forEach { stall ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 100.dp)
                                            .padding(vertical = 1.dp)
                                    ) {
                                        FoodStallItemCard(
                                            stall = stall,
                                            stallViewModel = viewModel,
                                            authViewModel = authViewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}