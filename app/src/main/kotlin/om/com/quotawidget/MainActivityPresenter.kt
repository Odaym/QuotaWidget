package om.com.quotawidget

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.jsoup.Jsoup

class MainActivityPresenter(
    private val view: MainActivityView,
    private val repository: Repository,
    private val backgroundScheduler: Scheduler,
    private val mainScheduler: Scheduler,
    private val compositeDisposable: CompositeDisposable
) {
    // Calls a Google Cloud Function
    fun triggerCloudFunction() = compositeDisposable.add(
        repository.triggerCloudFunction()
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribe {
                if (it != null) {
                    view.showUsageDetails(it)
                }
            }
    )

    fun login() = compositeDisposable.add(
        repository.login()
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribe {
                view.notifyLoginSuccess()
            }
    )

    fun getConsumption() {
        compositeDisposable.add(
            repository.getConsumption()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe {
                    val html = it.body()?.string().toString()
                    val document = Jsoup.parse(html)

                    val monthlyMax = document.select(MONTHLY_MAX_HTML_ELEMENT_ID).text()
                    val actualUsage = document.select(TOTAL_ACTUAL_USAGE_HTML_ELEMENT_ID).text()
                    val remaining = document.select(QUOTA_REMAINING_HTML_ELEMENT_ID).text()

                    view.showUsageDetails(UsageDetails(monthlyMax, actualUsage, remaining))
                }
        )
    }
}

interface MainActivityView {
    fun notifyLoginSuccess()
    fun showUsageDetails(it: UsageDetails)
}