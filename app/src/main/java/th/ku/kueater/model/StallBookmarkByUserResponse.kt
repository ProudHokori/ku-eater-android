package th.ku.kueater.model

data class StallBookmarkByUserResponse(
    val code: Int,
    val message: String,
    val userId: String,
    val bookmarks: List<StallBookmarkItem>
)

data class StallBookmarkItem(
    val id: String,
    val userId: String,
    val stallId: String,
    val timestamp: String,
    val stallDetails: StallItem
)