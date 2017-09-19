package biz.eventually.atpl

import android.content.Intent
import android.os.Bundle
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.SourceActivity
import biz.eventually.atpl.ui.source.SourceManager
import biz.eventually.atpl.utils.PREF_TIMER
import biz.eventually.atpl.utils.PREF_TOKEN
import biz.eventually.atpl.utils.PrefsPutString
import biz.eventually.atpl.utils.putLong
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton

// https://www.bignerdranch.com/blog/splash-screens-the-right-way/
class MainActivity : BaseActivity<SourceManager>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

        setContentView(R.layout.activity_splash)
        AtplApplication.component.inject(this)

        splash_version.text = "v${BuildConfig.VERSION_NAME}"

        // for App Links
        intent?.let {
            handleIntent(it)
        }

        // save preference waiting to do the screen
        putLong(applicationContext, PREF_TIMER, 1000)
    }

    private fun start() {
        rotateloading.start()
        manager.getSources(false, { s -> openSourceActivity(s) }, { openSourceActivity(null) })
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData = intent.data
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            val token = appLinkData.lastPathSegment

            token?.let {
                PrefsPutString(this@MainActivity, PREF_TOKEN, token)
                /*
                SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText(getString(R.string.dialog_title_ok))
                        .setContentText(getString(R.string.settings_api_saved))
                        .setCustomImage(R.drawable.ic_check)
                        .setConfirmClickListener({ start() })
                        .show()
                */
                alert(getString(R.string.settings_api_saved), getString(R.string.dialog_title_ok)) {
                    yesButton { start() }
                }.show()
            } ?: kotlin.run {
                /*
                SweetAlertDialog(this@MainActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.dialog_title_error))
                        .setContentText(getString(R.string.settings_api_error))
                        .setConfirmClickListener({ start() })
                        .show()
                 */
                alert(getString(R.string.settings_api_error), getString(R.string.dialog_title_error)) {
                    yesButton { start() }
                }.show()
            }

        } else {
            start()
        }
    }


    private fun openSourceActivity(sources: List<Source>?) {

        rotateloading.stop()

        when(sources) {
            null -> startActivity<SourceActivity>(IntentIdentifier.NETWORK_ERROR to true)
            else -> startActivity<SourceActivity>(IntentIdentifier.DATA_FROM_DB to true)
        }

        finish()
    }

}
