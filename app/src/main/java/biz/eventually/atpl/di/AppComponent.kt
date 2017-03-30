package biz.eventually.atpl.di

import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.di.module.AppModule
import biz.eventually.atpl.di.module.HttpModule
import biz.eventually.atpl.di.module.ServiceModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by laminr on 25/03/2017.
 */

@Singleton
@Component(modules = arrayOf(AppModule::class, HttpModule::class, ServiceModule::class))
interface AppComponent : AppGraph {

    /**
     * An initializer that creates the mock graph from an application.
     */
    class Initializer private constructor() {
        init {
            //throw UnsupportedOperationException()
        }

        companion object {

            fun init(app: AtplApplication): AppComponent {
                return DaggerAppComponent
                        .builder()
                        .appModule(AppModule(app))
                        .httpModule(HttpModule())
                        .serviceModule(ServiceModule())
                        .build()
            }
        }
    }
}