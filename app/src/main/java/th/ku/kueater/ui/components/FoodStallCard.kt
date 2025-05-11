package th.ku.kueater.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import th.ku.kueater.model.StallItem
import th.ku.kueater.viewmodel.AuthViewModel
import th.ku.kueater.viewmodel.StallViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FoodStallItemCard(
    stall: StallItem,
    authViewModel: AuthViewModel,
    stallViewModel: StallViewModel,
    onBookmarkToggle: (() -> Unit)? = null
) {
    val userId = authViewModel.getUserId() ?: ""

    // Get the screen height
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val cardHeight = screenHeight * 0.17f // 16% of the screen height (as per your code)

    val fallbackImageUrl = "https://res.cloudinary.com/dejzapat4/image/upload/v1743569783/spare_menu_pic_a7f2yr.png"
    val imageUrl = stall.cdnProfilePic.takeIf { !it.isNullOrBlank() } ?: fallbackImageUrl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight) // Set fixed height to 16% of screen height
            .padding(horizontal = 8.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize() // Ensure the Row fills the entire card
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = stall.nameEN ?: "Stall image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.4f)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )
            
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight() // Fill the full height of the card
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween // Distribute content vertically
            ) {
                // Top section: Name and Bookmark
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stall.nameEN ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF00695C)),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (stall.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (stall.isBookmarked) Color(0xFF00695C) else Color.Gray,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                val stallId = stall.location
                                if (onBookmarkToggle != null) {
                                    onBookmarkToggle()
                                }
                                stallViewModel.toggleBookmarkState(stallId = stallId, userId = userId)
                            }
                    )
                }

                // Bottom section: Details
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        stall.location.let {
                            Text(
                                text = "📍 $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Text(
                            text = "${stall.likeCount.coerceAtLeast(0)} Likes",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    stall.openCloseTime?.takeIf { it.isNotBlank() }?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🕒 $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    stall.foodTypeEN?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🍽 $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}