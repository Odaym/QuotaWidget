package om.com.quotawidget

import io.reactivex.Observable

interface Repository {
    fun getUsageDetails(): Observable<UsageDetails?>
}