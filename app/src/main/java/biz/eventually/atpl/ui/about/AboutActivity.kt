package biz.eventually.atpl.ui.about

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import biz.eventually.atpl.BuildConfig
import biz.eventually.atpl.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        about_version.text = "v${BuildConfig.VERSION_NAME}"

        val tangerine = Typeface.createFromAsset(assets, "fonts/Tangerine.ttf")
        about_by.typeface = tangerine
        about_dev_by.typeface = tangerine

        about_home.setOnClickListener { super.onBackPressed() }
    }
}
