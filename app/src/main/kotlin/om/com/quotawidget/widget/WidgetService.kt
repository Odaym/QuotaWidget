package om.com.quotawidget.widget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import om.com.quotawidget.NOTIFICATION_CHANNEL_NAME
import om.com.quotawidget.NOTIFICATION_NAME
import om.com.quotawidget.R
import om.com.quotawidget.USAGE_DETAILS_EXTRA
import om.com.quotawidget.data.UsageDetails
import org.jetbrains.anko.longToast
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException

class WidgetService : Service() {

    private lateinit var remoteViews: RemoteViews

    private val dcmlFormatter = DecimalFormat("0.#")
    private val nbrFormatter = NumberFormat.getInstance()

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = NOTIFICATION_CHANNEL_NAME

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        NOTIFICATION_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )

            startForeground(1, NotificationCompat.Builder(this, channelId).build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val usageDetails = intent.getParcelableExtra<UsageDetails>(USAGE_DETAILS_EXTRA)

        val allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

        for (widgetId in allWidgetIds) {
            remoteViews =
                    RemoteViews(
                        this.applicationContext.packageName,
                        R.layout.widget_layout
                    )

            with(usageDetails) {
                if (total_actual_usage != null) {
                    displayActualUsage(getFormattedNumber(total_actual_usage))
                }

                if (monthly_max != null) {
                    displayMonthlyMax(getFormattedNumber(monthly_max))
                }

                if (remaining_monthly_within_max != null && monthly_max != null) {
                    fillUpUsageBar(
                        getFormattedNumber(remaining_monthly_within_max),
                        getFormattedNumber(monthly_max)
                    )
                }
            }

            setupRefreshListener(allWidgetIds, widgetId)
        }

        stopSelf()

        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun displayActualUsage(totalUsed: Double) =
        remoteViews.setTextViewText(
            R.id.totalUsedQuotaTV,
            getString(
                R.string.used_quota,
                dcmlFormatter.format(String.format("%.2f", totalUsed).toDouble())
            )
        )

    private fun displayMonthlyMax(monthlyMax: Double) =
        remoteViews.setTextViewText(
            R.id.monthlyMaxTV,
            getString(
                R.string.monthly_max_quota,
                dcmlFormatter.format(String.format("%.2f", monthlyMax).toDouble()), "GB"
            )
        )

    private fun fillUpUsageBar(remainingQuota: Double, monthlyMax: Double) {
        val remainingPercentage = ((remainingQuota * 100) / monthlyMax).toInt()

        remoteViews.setTextViewText(
            R.id.remainingProgressValueTV,
            getString(
                R.string.remaining_quota_progress_value,
                remainingPercentage
            )
        )

        remoteViews.setProgressBar(
            R.id.quotaUsageProgressBar,
            100,
            remainingPercentage,
            false
        )
    }

    private fun setupRefreshListener(allWidgetIds: IntArray, widgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(
            this.applicationContext
        )

        val clickIntent = Intent(
            this.applicationContext,
            WidgetProvider::class.java
        )

        clickIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        clickIntent.putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_IDS,
            allWidgetIds
        )

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent)
        appWidgetManager.updateAppWidget(widgetId, remoteViews)
    }

    private fun getFormattedNumber(value: String): Double {
        var parsedNumber = 0.0

        try {
            parsedNumber = nbrFormatter.parse(value).toDouble() / 1000
        } catch (e: ParseException) {
            longToast(getString(R.string.html_elements_parse_exception_blame_idm))
        }

        return parsedNumber
    }
}