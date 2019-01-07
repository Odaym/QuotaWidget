package om.com.quotawidget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews

class WidgetService : Service() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()

            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val appWidgetManager = AppWidgetManager.getInstance(
            this.applicationContext
        )

        val allWidgetIds = intent
            .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

        val usageDetails = intent.getParcelableExtra<UsageDetails>("UsageDetails")


        //      ComponentName thisWidget = new ComponentName(getApplicationContext(),
        //              MyWidgetProvider.class);
        //      int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

        for (widgetId in allWidgetIds) {
            // create some random data

            val remoteViews =
                RemoteViews(this.applicationContext.packageName, R.layout.widget_layout)

            // Set the text
            remoteViews.setTextViewText(R.id.totalUsageTV, "Total usage ${usageDetails.total_actual_usage}")

            // Register an onClickListener
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
            remoteViews.setOnClickPendingIntent(R.id.totalUsageTV, pendingIntent)
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
        stopSelf()

        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}