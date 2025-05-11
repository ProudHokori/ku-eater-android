package th.ku.kueater.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import th.ku.kueater.viewmodel.AuthViewModel
import th.ku.kueater.viewmodel.StallBookmarkViewModel
import th.ku.kueater.viewmodel.StallViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SavedStallTabContent(
    authViewModel: AuthViewModel,
    bookmarkViewModel: StallBookmarkViewModel = viewModel(),
    stallViewModel: StallViewModel = viewModel(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val userId = authViewModel.getUserId() ?: return
    val savedStalls by bookmarkViewModel.savedStalls.collectAsState()
    val isLoading by bookmarkViewModel.isLoading.collectAsState()
    val error by bookmarkViewModel.error.collectAsState()

    LaunchedEffect(userId) {
        bookmarkViewModel.fetchSavedStalls(userId)
    }

    Column(modifier = modifier.fillMaxSize().background(tanColor)) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }
            savedStalls.isEmpty() -> {
                Text("No bookmarked stalls found", modifier = Modifier.padding(16.dp))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                ) {
                    items(savedStalls) { stall ->
                        FoodStallItemCard(stall = stall, stallViewModel = stallViewModel, authViewModel = authViewModel, onBookmarkToggle = {
                            bookmarkViewModel.toggleBookmarkState(stallId = stall.location ?: "")
                        })
                    }
                }
            }
        }
    }
}
