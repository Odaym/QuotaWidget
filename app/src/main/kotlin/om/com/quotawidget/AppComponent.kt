package om.com.quotawidget

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(app: RequestrApp)

    fun inject(widgetProvider: WidgetProvider)

    fun inject(httpClient: HttpClient)
}