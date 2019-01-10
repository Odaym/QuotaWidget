package om.com.quotawidget.data

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import om.com.quotawidget.COOKIE_REQUEST_HEADER_PROPERTY
import om.com.quotawidget.COOKIE_RESPONSE_HEADER_PROPERTY
import om.com.quotawidget.IDM_SESSION_COOKIE_PREF
import om.com.quotawidget.SERVER_BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class HttpClient(val prefs: SharedPreferences) {
    fun initializeHttpClient(): ApiService {
        val client = OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .followRedirects(false)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(ReceivedCookiesInterceptor())
            .addInterceptor(AddInfoInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addInterceptor { chain ->
                chain.proceed(chain.request())
            }
            .build()

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(SERVER_BASE_URL)
            .build()
            .create(ApiService::class.java)
    }

    inner class AddInfoInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder()

            val prefsSessionCookie = prefs.getStringSet(
                IDM_SESSION_COOKIE_PREF,
                HashSet()
            ) as HashSet

            builder.addHeader("Host", "customers.idm.net.lb")
            builder.addHeader("Connection", "keep-alive")
            builder.addHeader("Cache-Control", "max-age=0")
            builder.addHeader("Origin", "https://customers.idm.net.lb")
            builder.addHeader("Upgrade-Insecure-Requests", "1")
            builder.addHeader("Content-Type", "text/html; charset=utf-8")
            builder.addHeader("Referer", "https://customers.idm.net.lb/AcctManagement/")
            builder.addHeader("Accept-Language", "en-US,en;q=0.9")
            for (cookie in prefsSessionCookie) {
                builder.addHeader(
                    COOKIE_REQUEST_HEADER_PROPERTY,
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

            if (!originalResponse.headers(COOKIE_RESPONSE_HEADER_PROPERTY).isEmpty()) {
                val cookies = hashSetOf<String>()

                for (header in originalResponse.headers(COOKIE_RESPONSE_HEADER_PROPERTY)) {
                    cookies.add(header)
                }

                prefs.edit().putStringSet(IDM_SESSION_COOKIE_PREF, cookies).apply()
            }

            return originalResponse
        }
    }
}
