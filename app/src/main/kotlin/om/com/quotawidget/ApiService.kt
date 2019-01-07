package om.com.quotawidget

import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("getUsageDetails")
    fun getUsageDetails(): Observable<UsageDetails?>
}