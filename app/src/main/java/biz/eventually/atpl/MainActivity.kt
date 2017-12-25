package biz.eventually.atpl

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.ContextCompat
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.ViewModelFactory
import biz.eventually.atpl.ui.source.SourceActivity
import biz.eventually.atpl.ui.source.SourceRepository
import biz.eventually.atpl.ui.source.SourceViewModel
import biz.eventually.atpl.utils.Prefields
import biz.eventually.atpl.utils.prefsPutString
import cn.pedant.SweetAlert.SweetAlertDialog
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_questions.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainActivity : BaseActivity<SourceRepository>() {


    @Inject
    lateinit var sourceViewModelFactory: ViewModelFactory<SourceRepository>

    private lateinit var viewModel: SourceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

        setContentView(R.layout.activity_splash)
        AtplApplication.component.inject(this)

        splash_version.text = "v${BuildConfig.VERSION_NAME}"

        viewModel = ViewModelProviders.of(this, sourceViewModelFactory).get(SourceViewModel::class.java)

        // for App Links
        intent?.let {
            handleIntent(it)
        } ?: kotlin.run { start() }
    }

    private fun start() {

        val animation: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(applicationContext, R.drawable.props_rotation)
        splash_logo.setImageDrawable(animation)
        animation?.start()
        viewModel.refreshData()

        object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                viewModel.sources.observe(this@MainActivity, Observer<List<Source>> {
                    startActivity<SourceActivity>()
                    finish()
                })
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun startAnimation() {

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
}
