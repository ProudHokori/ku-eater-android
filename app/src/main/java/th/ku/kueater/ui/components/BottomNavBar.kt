package th.ku.kueater.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(val title: String, val route: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem("Overview", "overview", Icons.Default.Home),
    BottomNavItem("Random", "random", Icons.Default.Refresh),
    BottomNavItem("Saved", "saved", Icons.Default.List)
)

@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF2C7163), // dark green background
        tonalElevation = 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // rounded top corners
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo("overview") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selected) Color(0xFF00695C) else Color(0xFFE2EDEB)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = TextStyle(
                            color = if (selected) Color.White else Color(0xFFE2EDEB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                },
                alwaysShowLabel = true,
                modifier = Modifier
                    .then(
                        if (selected) {
                            Modifier
                                .drawWithContent {
                                    // Draw the semi-circle on top of the item
                                    val width = size.width
                                    val height = size.height
                                    val arcHeight = height * 0.3f // 30% of the item's height
                                    val arcRadius = width * 0.5f // Radius based on item width

                                    val path = Path().apply {
                                        // Start at the top-left
                                        moveTo(0f, arcHeight)
                                        // Draw left side down to bottom
                                        lineTo(0f, height)
                                        // Draw bottom line to the right
                                        lineTo(width, height)
                                        // Draw right side up to top
                                        lineTo(width, arcHeight)
                                        // Draw the semi-circle (arc) at the top
                                        arcTo(
                                            rect = androidx.compose.ui.geometry.Rect(
                                                left = 0f,
                                                top = -arcHeight, // Start above the top to create a semi-circle
                                                right = width,
                                                bottom = arcHeight
                                            ),
                                            startAngleDegrees = 180f,
                                            sweepAngleDegrees = -180f,
                                            forceMoveTo = false
                                        )
                                        close()
                                    }

                                    // Draw the path with a white fill to match the content background
                                    drawPath(
                                        path = path,
                                        color = Color.White,
                                        style = Fill
                                    )

                                    // Draw the content (icon and label) on top of the custom background
                                    drawContent()
                                }
                                .background(Color(0xFF2C7163)) // Maintain the dark green background
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}
