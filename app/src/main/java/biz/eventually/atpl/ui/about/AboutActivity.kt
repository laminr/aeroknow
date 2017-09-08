package biz.eventually.atpl.ui.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.BuildConfig
import biz.eventually.atpl.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        about_version.text = "v${BuildConfig.VERSION_NAME}"

        about_by.typeface = AtplApplication.tangerine
        about_dev_by.typeface = AtplApplication.tangerine

        about_home.setOnClickListener { super.onBackPressed() }
    }
}
