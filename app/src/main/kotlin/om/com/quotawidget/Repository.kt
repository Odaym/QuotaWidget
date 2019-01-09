package om.com.quotawidget

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response

interface Repository {
    fun triggerCloudFunction(): Observable<UsageDetails?>

    fun login(): Observable<Response<ResponseBody>>

    fun getConsumption(): Observable<Response<ResponseBody>>
}