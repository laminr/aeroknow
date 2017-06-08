package biz.eventually.atpl

import android.app.Application
import biz.eventually.atpl.data.db.checkRealmVersion
import biz.eventually.atpl.di.AppComponent
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import io.realm.Realm

/**
 * Created by laminr on 18/03/2017.
 */

class AtplApplication : Application() {

    lateinit var mFirebaseAnalytics : FirebaseAnalytics

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = AppComponent.Initializer.init(this)
        println("AtplApplication")

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Initialize Realm
        Realm.init(this)
        checkRealmVersion()

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
