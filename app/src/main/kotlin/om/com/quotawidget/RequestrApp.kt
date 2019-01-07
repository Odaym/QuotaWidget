package om.com.quotawidget

import android.app.Application
import timber.log.Timber

class RequestrApp : Application() {
    companion object {
        lateinit var appInstance: RequestrApp

        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)

        appInstance = this

        Timber.plant(Timber.DebugTree())
    }
}
