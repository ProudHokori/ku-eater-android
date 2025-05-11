package th.ku.kueater.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://script.google.com/macros/s/AKfycbzBRkhwNi_TTXSI5-vAOa2OHPfQZDOSzaN60d5Ddaun7tDeXdZv-ujIZojI_Nz3JCK4/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // 🔥 using custom gson
            .build()
    }

    val menuApi: MenuApiService by lazy {
        retrofit.create(MenuApiService::class.java)
    }

    val stallApi: StallApiService by lazy {
        retrofit.create(StallApiService::class.java)
    }

    val menuBookmarkApi: MenuBookmarkService by lazy {
        retrofit.create(MenuBookmarkService::class.java)
    }

    val stallBookmarkApi: StallBookmarkService by lazy {
        retrofit.create(StallBookmarkService::class.java)
    }

    val menuFeedbackApi: MenuFeedBackService by lazy {
        retrofit.create(MenuFeedBackService::class.java)
    }
}
