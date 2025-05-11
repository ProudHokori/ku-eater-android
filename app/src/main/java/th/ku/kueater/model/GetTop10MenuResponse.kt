package th.ku.kueater.model

data class GetTop10MenuResponse(
    val code: Int,
    val message: String,
    val data: List<MenuItem>
)