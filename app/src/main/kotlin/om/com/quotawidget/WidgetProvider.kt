package om.com.quotawidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WidgetProvider : AppWidgetProvider(), MainActivityView {
    @Inject
    lateinit var repository: Repository

    private var compositeDisposable = CompositeDisposable()
    private lateinit var presenter: MainActivityPresenter

    private lateinit var context: Context
    private lateinit var appWidgetManager: AppWidgetManager

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        this.context = context
        this.appWidgetManager = appWidgetManager

        QuotaWidget.component.inject(this)

        presenter = MainActivityPresenter(
            this,
            repository,
            Schedulers.newThread(),
            AndroidSchedulers.mainThread(),
            compositeDisposable
        )

        presenter.login()
    }

    override fun notifyLoginSuccess() = presenter.getConsumption()

    override fun showUsageDetails(it: UsageDetails) {
        val usageWidget = ComponentName(
            context,
            WidgetProvider::class.java
        )
        val allWidgetIds = appWidgetManager.getAppWidgetIds(usageWidget)

        val intent = Intent(
            context.applicationContext,
            WidgetService::class.java
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds)
        intent.putExtra(USAGE_DETAILS_EXTRA, it)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}