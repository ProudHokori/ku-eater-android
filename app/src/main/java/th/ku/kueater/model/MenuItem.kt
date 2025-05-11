package th.ku.kueater.model

import com.google.gson.annotations.SerializedName

data class MenuItem(
    @SerializedName("Menu ID") val id: String,
    @SerializedName("Stall Location") val stallLocation: String,
    @SerializedName("Stall Name EN") val stallName: String,
    @SerializedName("Menu EN") val menuName: String,
    @SerializedName("Price (Baht)") val price: Int,
    @SerializedName("Food Picture") val imageUrl: String,
    val isBookmarked: Boolean,
    val like: Int,          // Total number of likes for this menu item
    val dislike: Int,       // Total number of dislikes for this menu item
    val userFeedback: String? // User's feedback: "like", "dislike", or null
)