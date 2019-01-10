package om.com.quotawidget.data

import android.content.SharedPreferences
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response

class Repository(prefs: SharedPreferences) {
    private val apiService: ApiService = HttpClient(prefs).initializeHttpClient()

    fun triggerCloudFunction(): Observable<UsageDetails?> =
        apiService.triggerCloudFunction()

    fun login(): Observable<Response<ResponseBody>> =
        apiService.login(
            "",
            "",
            "/wEPDwUKLTk0NTAzMTA4Mw9kFgJmD2QWAgIBD2QWBAIBD2QWAgICD2QWAgIBDw9kFgIeBVN0eWxlBStjb2xvcjpSZWQ7Zm9udC1mYW1pbHk6QXJpYWw7Zm9udC1zaXplOjEwcHQ7ZAIDDxYCHgdWaXNpYmxlZ2RkCgNxpzWaf0MHD+rzMJ+x++b1LfUyzx4oZWPmSdECXnU=",
            "DF9634BE",
            "/wEdAAjSMEeTNA+vDf/63C4d5PgHlyagtnqh9v2mox23Jck7iNacDqQoN8Jvpp8g4bp+MhQWM2rje1Lql59I6DbuFzq0aC7HPLjEIsal8G7u+GCq0QRjV/6OhcAmk5AQTJHvewEE/CzrOuqj4sBN5UJctExBnW1LMq1xMuh9PQP3dfWtl1647j8B9lWf6ypqZiT9mQ0iVOCS/7kwWbuPGtfM8bRH",
            "L474252",
            "oaVmrZuz",
            "Login",
            ""
        )

    fun getConsumption(): Observable<Response<ResponseBody>> =
        apiService.getConsumption()
}