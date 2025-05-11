package th.ku.kueater.ui.screen

import FoodTypeSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import th.ku.kueater.ui.components.BottomNavBar
import th.ku.kueater.ui.components.MenuCard
import th.ku.kueater.viewmodel.AuthViewModel
import th.ku.kueater.viewmodel.BookmarkViewModel
import th.ku.kueater.viewmodel.MenuViewModel

// Define colors to match your mockup
val tanColor = Color(0xFFFDF5E6) // Light tan color
val titleColor = Color(0xFF00695C) // Title text color

@Composable
fun RandomScreen(navController: NavController) {
    // Get required view models.
    val menuViewModel: MenuViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val bookmarkViewModel: BookmarkViewModel = viewModel()

    val userId = authViewModel.getUserId()

    LaunchedEffect(userId) {
        if (userId != null) {
            menuViewModel.setUserId(userId)
        }
    }

    // List of food types.
    val foodTypes = listOf(
        "Curry",
        "Topping",
        "Seafood",
        "Cook-to-order",
        "Noodles",
        "Soup",
        "Snacks",
        "Rice",
        "Salad",
        "Sandwiches",
        "Pasta",
        "Sushi",
        "Chili Dip",
        "Rice & Salad",
        "Beverages",
        "Desserts",
        "Fruit",
        "Dim Sum",
        "Add-on",
        "Beverage"
    )

    var selectedFoodType by remember { mutableStateOf("") }

    // Local state to track random menu loading
    var isRandomMenuLoading by remember { mutableStateOf(false) }

    // Observe random menu state.
    val randomMenu = menuViewModel.randomMenu.collectAsState().value

    // Use LocalConfiguration to get the screen height in dp.
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                modifier = Modifier
                    .background(Color(0xFF14524A))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1) Top Title Section (tan background)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tanColor)
                    .padding(top = 36.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Let's Random your Menu?!",
                    color = titleColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 2) Middle Section (green background) taking only about 56% of the screen height.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.56f)
                    .background(titleColor),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isRandomMenuLoading -> {
                        CircularProgressIndicator(color = Color.White)
                    }
                    randomMenu != null -> {
                        MenuCard(
                            item = randomMenu,
                            authViewModel = authViewModel,
                            bookmarkViewModel = bookmarkViewModel,
                            menuViewModel = menuViewModel,
                            width = 300.dp,
                            height = 360.dp,
                            forBookmarkPage = true,
                        )
                    }
                    else -> {
                        Text(
                            text = "No random menu yet!",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3) Bottom Section (tan background) that spans the rest of the screen.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // Takes the remaining vertical space until the end.
                    .background(tanColor)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom, // Align children to the bottom
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight() // Ensure the Row doesn't expand vertically unnecessarily
                ) {
                    FoodTypeSelector(
                        foodTypes = foodTypes,
                        selectedFoodType = selectedFoodType,
                        onFoodTypeSelected = { selectedFoodType = it },
                        width = 200.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = {
                            if (selectedFoodType.isNotEmpty()) {
                                isRandomMenuLoading = true
                                menuViewModel.getRandomMenu(
                                    selectedFoodType,
                                    onSuccess = { isRandomMenuLoading = false },
                                    onError = { isRandomMenuLoading = false }
                                )
                            }
                        },
                        enabled = selectedFoodType.isNotEmpty(),
                        modifier = Modifier
                            .size(56.dp)
                            .background(titleColor, shape = RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Casino,
                            contentDescription = "Get Random Menu",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
