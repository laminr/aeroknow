package biz.eventually.atpl.di.module

import android.content.Context

import javax.inject.Singleton

import biz.eventually.atpl.AtplApplication
import dagger.Module
import dagger.Provides

/**
 * Created by laminr on 18/03/2017.
 */
@Module
class AppModule(val app: AtplApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return app
    }

    @Provides
    @Singleton
    fun provideApplication(): AtplApplication {
        return app
    }
}
