package th.ku.kueater.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import th.ku.kueater.R
import th.ku.kueater.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // Navigation logic
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            delay(1000) // Short delay for UX
            navController.navigate(if (isLoggedIn == true) "overview" else "login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // UI matching the mockup
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A5A5A)), // Teal background color
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ku_eater_landing_logo),
            contentDescription = "KU Eater Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f) // Adjust the size to 50% of the screen width
        )
    }
}