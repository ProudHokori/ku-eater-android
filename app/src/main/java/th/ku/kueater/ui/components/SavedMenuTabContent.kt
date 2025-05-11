package th.ku.kueater.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import th.ku.kueater.viewmodel.AuthViewModel
import th.ku.kueater.viewmodel.BookmarkViewModel

@Composable
fun SavedMenuTabContent(
    authViewModel: AuthViewModel,
    bookmarkViewModel: BookmarkViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val tanColor = Color(0xFFFDF5E6)
    val userId = authViewModel.getUserId() ?: return
    val savedMenus by bookmarkViewModel.savedMenus.collectAsState()
    val isLoading by bookmarkViewModel.isLoading.collectAsState()
    val error by bookmarkViewModel.error.collectAsState()

    LaunchedEffect(userId) {
        bookmarkViewModel.fetchSavedMenus(userId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tanColor)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }
            savedMenus.isEmpty() -> {
                Text("No bookmarked menus found", modifier = Modifier.padding(16.dp))
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedMenus.size) { index ->
                        val menu = savedMenus[index]
                        MenuCard(
                            item = menu,
                            authViewModel = authViewModel,
                            bookmarkViewModel = bookmarkViewModel,
                            onUserFeedBackChange = bookmarkViewModel::updateFeedbackState,
                            width = 200.dp, // Example width
                            height = 325.dp, // Example height
                            forBookmarkPage = true
                        )
                    }
                }
            }
        }
    }
}
