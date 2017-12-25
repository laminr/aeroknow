package biz.eventually.atpl.ui.about

import android.os.Bundle
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.app.AppCompatActivity
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.BuildConfig
import biz.eventually.atpl.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    private var animation : AnimatedVectorDrawableCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        about_version.text = "v${BuildConfig.VERSION_NAME}"

        about_by.typeface = AtplApplication.tangerine
        about_dev_by.typeface = AtplApplication.tangerine

        about_home.setOnClickListener { super.onBackPressed() }

        animation = AnimatedVectorDrawableCompat.create(applicationContext, R.drawable.props_rotation)
        about_logo.setImageDrawable(animation)
        animation?.start()

        about_name.setOnClickListener {
            when (animation?.isRunning) {
                true -> animation?.stop()
                false -> animation?.start()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        animation?.stop()
    }
}
