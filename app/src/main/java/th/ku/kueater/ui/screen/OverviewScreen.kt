package th.ku.kueater.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import th.ku.kueater.R
import th.ku.kueater.ui.components.BottomNavBar
import th.ku.kueater.ui.components.FoodStallTabContent
import th.ku.kueater.ui.components.MenuTabContent
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.lifecycle.viewmodel.compose.viewModel
import th.ku.kueater.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Menu", "Food Stall")
    val authViewModel: AuthViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                modifier = Modifier // Pass the modifier parameter
                    .background(Color(0xFF14524A)) // Match the bottom nav color from the mockup
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // Rounded top corners
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ku_eater_header_logo),
                    contentDescription = "KU Eater Logo",
                    modifier = Modifier
                        .height(36.dp)
                        .align(Alignment.Center)
                )

                IconButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("overview") { inclusive = true } // Prevent going back to Overview
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFF2C7163)  // Your app green
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF2C7163),
                indicator = { tabPositions ->
                    val currentTab = tabPositions[selectedTab]
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(currentTab)
                            .padding(horizontal = (currentTab.width / 8)) // Shrinks width
                            .offset(y = (-12).dp) // Move underline up
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF2C7163))
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == index) Color(0xFF2C7163) else Color.Gray,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 12.dp) // Adds space below the tab text
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> MenuTabContent()
                1 -> FoodStallTabContent()
            }
        }
    }
}