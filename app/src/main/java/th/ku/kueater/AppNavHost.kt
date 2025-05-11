package th.ku.kueater

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import th.ku.kueater.ui.screen.LoginScreen
import th.ku.kueater.ui.screen.OverviewScreen
import th.ku.kueater.ui.screen.RandomScreen
import th.ku.kueater.ui.screen.RegisterScreen
import th.ku.kueater.ui.screen.SavedListScreen
import th.ku.kueater.ui.screen.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("overview") { OverviewScreen(navController) }
        composable("random") { RandomScreen(navController) }
        composable("saved") { SavedListScreen(navController) }

    }
}