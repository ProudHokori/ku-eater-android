package th.ku.kueater.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import th.ku.kueater.model.PostStallBookmarkPayload
import th.ku.kueater.model.StallBookmarkByUserResponse

interface StallBookmarkService {
    @POST("exec?route=stall_bookmark")
    suspend fun toggleStallBookmark(@Body request: PostStallBookmarkPayload): Response<Unit>

    @GET("exec")
    suspend fun getStallBookmarksByUser(
        @Query("route") route: String = "stall_bookmark_by_user",
        @Query("userId") userId: String
    ): StallBookmarkByUserResponse
}