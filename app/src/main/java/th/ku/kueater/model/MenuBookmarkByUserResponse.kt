package th.ku.kueater.model

data class MenuBookmarkByUserResponse(
    val code: Int,
    val message: String,
    val userId: String,
    val bookmarks: List<MenuBookmarkItem>
)

data class MenuBookmarkItem(
    val id: String,
    val menuId: String,
    val timestamp: String,
    val userId: String,
    val menuDetails: MenuItem
)
