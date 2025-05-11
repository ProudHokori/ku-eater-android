package th.ku.kueater.network

import retrofit2.http.GET
import retrofit2.http.Query
import th.ku.kueater.model.GetRandomMenuResponse
import th.ku.kueater.model.GetTop10MenuResponse
import th.ku.kueater.model.MenuResponse

interface MenuApiService {
    @GET("exec")
    suspend fun getMenus(
        @Query("route") route: String = "menu_table",
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 10,
        @Query("userId") userId: String
    ): MenuResponse

    @GET("exec")
    suspend fun getTop10Menus(
        @Query("route") route: String = "get_top_10_menu",
        @Query("userId") userId: String
    ): GetTop10MenuResponse

    @GET("exec")
    suspend fun getRandomMenu(
        @Query("route") route: String = "get_random_menu",
        @Query("foodType") foodType: String,
        @Query("userId") userId: String
    ): GetRandomMenuResponse
}