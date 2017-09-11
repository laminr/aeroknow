package biz.eventually.atpl

import android.app.Application
import android.graphics.Typeface
import android.support.multidex.MultiDex
import biz.eventually.atpl.data.db.checkRealmVersion
import biz.eventually.atpl.di.AppComponent
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import io.realm.Realm

/**
 * Created by laminr on 18/03/2017.
 */

class AtplApplication : Application() {

    private lateinit var mFirebaseAnalytics : FirebaseAnalytics

    companion object {
        lateinit var component: AppComponent
        lateinit var tangerine : Typeface
    }

    override fun onCreate() {
        super.onCreate()

        component = AppComponent.Initializer.init(this)
        tangerine = Typeface.createFromAsset(assets, "fonts/Tangerine.ttf")

        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Initialize Realm
        Realm.init(this)
        checkRealmVersion()

        MultiDex.install(this)

        /*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        */
    }

}
