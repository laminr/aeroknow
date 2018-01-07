package biz.eventually.atpl.di

import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.di.module.*
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 25/03/2017.
 */

@Singleton
@Component(modules = [
    AppModule::class,
    HttpModule::class,
    ServiceModule::class,
    DatabaseModule::class,
    ViewModelModule::class
])
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