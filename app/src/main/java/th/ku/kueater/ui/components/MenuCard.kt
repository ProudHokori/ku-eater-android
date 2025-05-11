package th.ku.kueater.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import th.ku.kueater.model.MenuItem
import th.ku.kueater.model.PostMenuBookmarkPayload
import th.ku.kueater.network.RetrofitInstance
import th.ku.kueater.viewmodel.AuthViewModel
import th.ku.kueater.viewmodel.BookmarkViewModel
import th.ku.kueater.viewmodel.MenuViewModel
import kotlin.reflect.KFunction4

@Composable
fun MenuCard(
    item: MenuItem,
    authViewModel: AuthViewModel,
    bookmarkViewModel: BookmarkViewModel,
    menuViewModel: MenuViewModel = viewModel(),
    onUserFeedBackChange: KFunction4<String, Int, Int, String?, Unit>? = null,
    width: Dp, // New width prop
    height: Dp, // New height prop
    forBookmarkPage: Boolean = false
) {
    val green = Color(0xFF00695C)
    val red = Color.Red

    // Local states for optimistic updates
    val stateKey = remember(item.id, item.like, item.dislike, item.userFeedback, item.isBookmarked) {
        "${item.id}-${item.like}-${item.dislike}-${item.userFeedback}-${item.isBookmarked}"
    }

    var isBookmarked by remember(stateKey) { mutableStateOf(item.isBookmarked) }
    var liked by remember(stateKey) { mutableStateOf(item.userFeedback == "like") }
    var disliked by remember(stateKey) { mutableStateOf(item.userFeedback == "dislike") }
    var likeCount by remember(stateKey) { mutableIntStateOf(item.like) }
    var dislikeCount by remember(stateKey) { mutableIntStateOf(item.dislike) }

    val userId = authViewModel.getUserId() ?: ""
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val fallbackImageUrl = "https://res.cloudinary.com/dejzapat4/image/upload/v1743569783/spare_menu_pic_a7f2yr.png"
    val imageUrl = item.imageUrl.takeIf { !it.isNullOrBlank() } ?: fallbackImageUrl

    Card(
        modifier = Modifier
            .width(width) // Apply fixed width
            .height(height) // Apply fixed height
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Ensure the Column fills the entire Card
        ) {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take up available space proportionally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = item.menuName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) Color.Red else Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clickable {
                            val previousState = isBookmarked
                            isBookmarked = !isBookmarked

                            if (forBookmarkPage) {
                                scope.launch {
                                    try {
                                        val response = RetrofitInstance.menuBookmarkApi.toggleMenuBookmark(
                                            PostMenuBookmarkPayload(userId = userId, menuId = item.id)
                                        )
                                        if (!response.isSuccessful) {
                                            isBookmarked = previousState
                                            Toast.makeText(context, "Failed to toggle bookmark", Toast.LENGTH_SHORT).show()
                                        } else {
                                            bookmarkViewModel.toggleBookmarkState(item.id)
                                        }
                                    } catch (e: Exception) {
                                        isBookmarked = previousState
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                menuViewModel.postMenuBookmark(
                                    menuId = item.id,
                                    isCurrentlyBookmarked = previousState,
                                    onSuccess = {
                                        // Optimistically already updated
                                        bookmarkViewModel.toggleBookmarkState(item.id)
                                    },
                                    onError = { error ->
                                        isBookmarked = previousState
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                )

            }

            // Text and feedback section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take up available space proportionally
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween // Distribute content vertically
            ) {
                // Top section: Menu name and stall info
                Column {
                    Text(
                        text = item.menuName,
                        style = MaterialTheme.typography.titleMedium.copy(color = green)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\uD83C\uDF7D ${item.stallLocation}  ${item.stallName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Bottom section: Price and feedback
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${item.price} ฿",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, green, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    // Store previous state for rollback
                                    val previousLiked = liked
                                    val previousDisliked = disliked
                                    val previousLikeCount = likeCount
                                    val previousDislikeCount = dislikeCount

                                    // Optimistically update the UI
                                    if (liked) {
                                        // Remove like
                                        liked = false
                                        likeCount -= 1
                                    } else {
                                        // Add like
                                        liked = true
                                        likeCount += 1
                                        if (disliked) {
                                            disliked = false
                                            dislikeCount -= 1
                                        }
                                    }

                                    onUserFeedBackChange?.invoke(
                                        item.id,
                                        likeCount,
                                        dislikeCount,
                                        if (liked) "like" else null
                                    )

                                    // Make the API call
                                    menuViewModel.postMenuFeedback(
                                        menuId = item.id,
                                        feedbackType = "like",
                                        onSuccess = {
                                            // Feedback updated successfully
                                        },
                                        onError = { error ->
                                            // Revert the UI on error
                                            liked = previousLiked
                                            disliked = previousDisliked
                                            likeCount = previousLikeCount
                                            dislikeCount = previousDislikeCount
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        },
                                        userId = userId
                                    )
                                }
                                .padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Like",
                                tint = if (liked) green else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = likeCount.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(text = " | ", color = Color.Gray)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    // Store previous state for rollback
                                    val previousLiked = liked
                                    val previousDisliked = disliked
                                    val previousLikeCount = likeCount
                                    val previousDislikeCount = dislikeCount

                                    // Optimistically update the UI
                                    if (disliked) {
                                        // Remove dislike
                                        disliked = false
                                        dislikeCount -= 1
                                    } else {
                                        // Add dislike
                                        disliked = true
                                        dislikeCount += 1
                                        if (liked) {
                                            liked = false
                                            likeCount -= 1
                                        }
                                    }

                                    onUserFeedBackChange?.invoke(
                                        item.id,
                                        likeCount,
                                        dislikeCount,
                                        if (disliked) "dislike" else null
                                    )

                                    // Make the API call
                                    menuViewModel.postMenuFeedback(
                                        menuId = item.id,
                                        feedbackType = "dislike",
                                        onSuccess = {
                                            // Feedback updated successfully
                                        },
                                        onError = { error ->
                                            // Revert the UI on error
                                            liked = previousLiked
                                            disliked = previousDisliked
                                            likeCount = previousLikeCount
                                            dislikeCount = previousDislikeCount
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        },
                                        userId = userId
                                    )
                                }
                                .padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbDown,
                                contentDescription = "Dislike",
                                tint = if (disliked) red else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dislikeCount.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}