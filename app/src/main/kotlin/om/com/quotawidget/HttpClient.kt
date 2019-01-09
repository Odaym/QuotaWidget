package om.com.quotawidget

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HttpClient {
    @Inject
    lateinit var prefs: SharedPreferences

    companion object {
        const val CURRENT_SERVER_ADDRESS: String =
            "https://customers.idm.net.lb/AcctManagement/"
    }

    fun initializeHttpClient(): ApiService {
        QuotaWidget.component.inject(this)

        val client = OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(AddInfoInterceptor())
            .addInterceptor(ReceivedCookiesInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addInterceptor { chain ->
                chain.proceed(chain.request())
            }
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .baseUrl(CURRENT_SERVER_ADDRESS)
            .build()
            .create(ApiService::class.java)
    }

    inner class AddInfoInterceptor : Interceptor {
        @SuppressLint("HardwareIds")
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder()

            val prefsSessionCookie = prefs.getStringSet(
                "session_cookie",
                HashSet()
            ) as HashSet

            builder.addHeader("Host", "customers.idm.net.lb")
            builder.addHeader("Connection", "keep-alive")
            builder.addHeader("Cache-Control", "max-age=0")
            builder.addHeader("Origin", "https://customers.idm.net.lb")
            builder.addHeader("Upgrade-Insecure-Requests", "1")
            builder.addHeader("Content-Type", "text/html; charset=utf-8")
            builder.addHeader("Referer", "https://customers.idm.net.lb/AcctManagement/")
//            builder.addHeader("Accept-Encoding", "gzip, deflate, br")
            builder.addHeader("Accept-Language", "en-US,en;q=0.9")
            for (cookie in prefsSessionCookie) {
                builder.addHeader(
                    "Cookie",
                    cookie
                )
            }
            builder.addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"
            )
            builder.addHeader(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
            )

            return chain.proceed(builder.build())
        }
    }

    inner class ReceivedCookiesInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse = chain.proceed(chain.request())

            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                val cookies = hashSetOf<String>()

                for (header in originalResponse.headers("Set-Cookie")) {
                    cookies.add(header)
                }

                Timber.d("Cookies - $cookies")

                prefs.edit().putStringSet("session_cookie", cookies).apply()
            }

            return originalResponse
        }
    }
}
