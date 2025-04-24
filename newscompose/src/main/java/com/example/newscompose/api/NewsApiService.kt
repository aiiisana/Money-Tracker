import com.example.newscompose.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    suspend fun getNewsByKeyword(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String = "en"
    ): NewsResponse
}