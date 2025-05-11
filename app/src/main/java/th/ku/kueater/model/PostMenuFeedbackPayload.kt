package th.ku.kueater.model

data class PostMenuFeedbackPayload(
    val userId: String,
    val menuId: String,
    val feedbackType: String
)