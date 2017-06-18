package biz.eventually.atpl

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.data.model.Source
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.SourceActivity
import biz.eventually.atpl.ui.source.SourceManager
import java.util.*

import kotlinx.android.synthetic.main.activity_splash.*
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

// https://www.bignerdranch.com/blog/splash-screens-the-right-way/
class MainActivity : BaseActivity<SourceManager>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

        setContentView(R.layout.activity_splash)
        AtplApplication.component.inject(this)

        splash_version.text = "v${BuildConfig.VERSION_NAME}"
        rotateloading.start()
        manager.getSources({ s -> openSourceActivity(s)}, { openSourceActivity(null)})
    }

    fun openSourceActivity(sources: List<Source>?) {

        rotateloading.stop()
        val intent = Intent(this, SourceActivity::class.java)

        when(sources) {
            null -> intent.putExtra(IntentIdentifier.NETWORK_ERROR, true)
            else -> intent.putParcelableArrayListExtra(IntentIdentifier.SOURCE_LIST, sources as ArrayList<Source>)
        }

        startActivity(intent)
        finish()
    }

}
