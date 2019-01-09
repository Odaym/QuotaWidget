package om.com.quotawidget

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: QuotaWidget) {
    @Provides
    @Singleton
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRepository(): Repository = RepositoryImpl()

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences = app.getSharedPreferences(
        "Prefs",
        Context.MODE_PRIVATE
    )
}