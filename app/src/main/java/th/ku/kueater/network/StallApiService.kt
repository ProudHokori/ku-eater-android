package th.ku.kueater.network

import retrofit2.http.GET
import retrofit2.http.Query
import th.ku.kueater.model.StallResponse

interface StallApiService {
    @GET("exec")
    suspend fun getAllStalls(
        @Query("route") route: String = "stall_table",
        @Query("userId") userId: String
    ): StallResponse
}
