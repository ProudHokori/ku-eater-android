package th.ku.kueater.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import th.ku.kueater.model.MenuBookmarkByUserResponse
import th.ku.kueater.model.PostMenuBookmarkPayload

interface MenuBookmarkService {
    @POST("exec?route=menu_bookmark")
    suspend fun toggleMenuBookmark(@Body request: PostMenuBookmarkPayload): Response<Unit>

    @GET("exec")
    suspend fun getMenuBookmarksByUser(
        @Query("route") route: String = "menu_bookmark_by_user",
        @Query("userId") userId: String
    ): MenuBookmarkByUserResponse
}