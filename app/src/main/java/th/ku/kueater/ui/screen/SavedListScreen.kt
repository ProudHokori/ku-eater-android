package th.ku.kueater.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import th.ku.kueater.ui.components.BottomNavBar
import th.ku.kueater.ui.components.SavedMenuTabContent
import th.ku.kueater.ui.components.SavedStallTabContent
import th.ku.kueater.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SavedListScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Menu", "Food Stall")

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
            // Header with text and icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved list",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C7163) // Match the dark green color from the mockup
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Saved icon",
                    tint = Color(0xFF2C7163),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Styled Tab Row (copied from OverviewScreen)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF2C7163),
                indicator = { tabPositions ->
                    val currentTab = tabPositions[selectedTab]
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(currentTab)
                            .padding(horizontal = (currentTab.width / 8)) // Match shrink effect
                            .offset(y = (-12).dp) // Move indicator up
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
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    )
                }
            }

            // Tab content
            when (selectedTab) {
                0 -> SavedMenuTabContent(
                    authViewModel = authViewModel,
                    modifier = Modifier.fillMaxSize()
                )
                1 -> SavedStallTabContent(
                    authViewModel = authViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}