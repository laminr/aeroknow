package biz.eventually.atpl

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.support.multidex.BuildConfig
import android.support.multidex.MultiDex
import biz.eventually.atpl.di.AppComponent
import com.facebook.stetho.Stetho
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber


/**
 * Created by Thibault de Lambilly on 18/03/2017.
 *
 */

class AtplApplication : Application() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    companion object {
        lateinit var component: AppComponent
        lateinit var instance: AtplApplication
        lateinit var tangerine: Typeface

        fun get(): Application {
            return instance
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        component = AppComponent.Initializer.init(this)
        instance = this

        tangerine = Typeface.createFromAsset(assets, "fonts/Tangerine.ttf")

        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        Stetho.initializeWithDefaults(this)
        Timber.plant(Timber.DebugTree())

        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }

    }

}
