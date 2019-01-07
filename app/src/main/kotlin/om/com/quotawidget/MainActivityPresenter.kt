package om.com.quotawidget

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class MainActivityPresenter(
    private val view: MainActivityView,
    private val repository: Repository,
    private val backgroundScheduler: Scheduler,
    private val mainScheduler: Scheduler,
    private val compositeDisposable: CompositeDisposable
) {
    fun getUsageDetails() = compositeDisposable.add(
        repository.getUsageDetails()
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribe {
                if (it != null){
                    view.showUsageDetails(it)
                }
            }
    )
}

interface MainActivityView {
    fun showUsageDetails(it: UsageDetails)
}