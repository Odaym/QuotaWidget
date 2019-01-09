package om.com.quotawidget

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(app: QuotaWidget)

    fun inject(widgetProvider: WidgetProvider)

    fun inject(httpClient: HttpClient)
}