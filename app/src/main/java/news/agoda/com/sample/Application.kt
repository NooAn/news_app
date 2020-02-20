package news.agoda.com.sample

import android.app.Application
import news.agoda.com.sample.di.appComponents
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appComponents)
            AndroidLogger(Level.DEBUG)
        }
    }
}