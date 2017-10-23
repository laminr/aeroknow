package biz.eventually.atpl

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.SourceActivity
import biz.eventually.atpl.ui.source.SourceRepository
import biz.eventually.atpl.ui.source.SourceViewModel
import biz.eventually.atpl.ui.source.ViewModelFactory
import biz.eventually.atpl.utils.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainActivity : BaseActivity<SourceRepository>() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SourceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

        setContentView(R.layout.activity_splash)
        AtplApplication.component.inject(this)

        splash_version.text = "v${BuildConfig.VERSION_NAME}"

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SourceViewModel::class.java)

        // for App Links
        intent?.let {
            handleIntent(it)
        } ?: kotlin.run { start() }
    }

    private fun start() {
        splash_rotating_left.start()
        splash_rotating_right.start()

        viewModel.data.observe(this, Observer<List<Source>> {
            openSourceActivity()
        })
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData = intent.data
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            val token = appLinkData.lastPathSegment

            token?.let {
                prefsPutString(this@MainActivity, Prefields.PREF_TOKEN, token)

                SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText(getString(R.string.dialog_title_ok))
                        .setContentText(getString(R.string.settings_api_saved))
                        .setCustomImage(R.drawable.ic_check)
                        .setConfirmClickListener({ start() })
                        .show()
            } ?: kotlin.run {
                SweetAlertDialog(this@MainActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.dialog_title_error))
                        .setContentText(getString(R.string.settings_api_error))
                        .setConfirmClickListener({ start() })
                        .show()
            }

        } else {
            start()
        }
    }


    private fun openSourceActivity() {

        splash_rotating_left.stop()
        splash_rotating_right.stop()

        startActivity<SourceActivity>()

//        when(hasData) {
//            true -> startActivity<SourceActivity>(IntentIdentifier.DATA_FROM_DB to true)
//            false -> startActivity<SourceActivity>(IntentIdentifier.NETWORK_ERROR to true)
//        }

        finish()
    }

}
