package om.com.quotawidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import om.com.quotawidget.APPLICATION_PREFS_KEY
import om.com.quotawidget.USAGE_DETAILS_EXTRA
import om.com.quotawidget.data.Repository
import om.com.quotawidget.data.UsageDetails

class WidgetProvider : AppWidgetProvider(), WidgetProviderView {
    private lateinit var repository: Repository

    private lateinit var prefs: SharedPreferences

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

        prefs = context.getSharedPreferences(
            APPLICATION_PREFS_KEY,
            Context.MODE_PRIVATE
        )

        repository = Repository(prefs)

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