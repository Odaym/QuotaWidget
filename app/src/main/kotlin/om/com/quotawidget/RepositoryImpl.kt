package om.com.quotawidget

import io.reactivex.Observable

class RepositoryImpl(app: RequestrApp) : Repository {
    private val apiService: ApiService = HttpClient().initializeHttpClient(app)

    override fun getUsageDetails(): Observable<UsageDetails?> =
        apiService.getUsageDetails()
}