package th.ku.kueater.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import th.ku.kueater.model.PostMenuFeedbackPayload

interface MenuFeedBackService {
    @POST("exec?route=menu_feedback")
    suspend fun postMenuFeedback(
        @Body payload: PostMenuFeedbackPayload
    ): Response<Unit>
}