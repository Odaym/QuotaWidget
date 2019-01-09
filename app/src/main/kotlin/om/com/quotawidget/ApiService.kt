package om.com.quotawidget

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("ScrapeQuota")
    fun triggerCloudFunction(): Observable<UsageDetails?>

    @FormUrlEncoded
    @POST("Login.aspx")
    fun login(
        @Field("__EVENTTARGET") eventTarget: String,
        @Field("__EVENTARGUMENT") eventArgument: String,
        @Field("__VIEWSTATE") viewState: String,
        @Field("__VIEWSTATEGENERATOR") viewStateGenerator: String,
        @Field("__EVENTVALIDATION") eventValidation: String,
        @Field("ctl00\$ContentPlaceHolder1\$txtuser") txtUser: String,
        @Field("ctl00\$ContentPlaceHolder1\$txtpassword") txtPassword: String,
        @Field("ctl00\$ContentPlaceHolder1\$btnlogin") btnLogin: String,
        @Field("ctl00\$ContentPlaceHolder1\$txtCPE") txtCPE: String
    ): Observable<Response<ResponseBody>>

    @GET("ADSLConsumption.aspx")
    fun getConsumption(): Observable<Response<ResponseBody>>

}