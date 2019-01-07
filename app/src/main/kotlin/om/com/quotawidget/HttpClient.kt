package om.com.quotawidget

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class HttpClient {

    companion object {
        const val CURRENT_SERVER_ADDRESS: String = "https://us-central1-requestr.cloudfunctions.net/"
    }

    fun initializeHttpClient(app: RequestrApp): ApiService {
        RequestrApp.component.inject(this)

        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cache = Cache(app.cacheDir, cacheSize)

        val client = OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
//            .addInterceptor(AddInfoInterceptor(prefs))
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addInterceptor { chain ->
                try {
                    chain.proceed(chain.request())
                } catch (e: Exception) {
                    val offlineRequest = chain.request().newBuilder()
                        .header(
                            "Cache-Control", "public, only-if-cached," +
                                    "max-stale=" + 60 * 60
                        )
                        .build()
                    chain.proceed(offlineRequest)
                }
            }
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(CURRENT_SERVER_ADDRESS)
            .build()
            .create(ApiService::class.java)
    }
}
