package th.ku.kueater.model

import com.google.gson.annotations.SerializedName

data class StallItem(
    @SerializedName("Stall Location") val location: String,
    @SerializedName("Stall Name TH") val nameTH: String? = null,
    @SerializedName("Stall Name EN") val nameEN: String? = null,
    @SerializedName("CDN profile pic") val cdnProfilePic: String? = null,
    @SerializedName("Stall food type EN") val foodTypeEN: String? = null,
    @SerializedName("Open time") val openTimeFormatted: String? = null,
    @SerializedName("Close time") val closeTimeFormatted: String? = null,
    val openCloseTime: String? = null,
    val isBookmarked: Boolean, // <-- NEW FIELD
    val likeCount: Int = 0,
)
