package biz.eventually.atpl

import android.content.Intent
import android.os.Bundle
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.data.model.Source
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.SourceActivity
import biz.eventually.atpl.ui.source.SourceManager
import java.util.*

import kotlinx.android.synthetic.main.activity_splash.*

// https://www.bignerdranch.com/blog/splash-screens-the-right-way/
class MainActivity : BaseActivity<SourceManager>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        AtplApplication.component.inject(this)
        rotateloading.start()
        manager.getSources({ s -> openSourceActivity(s)}, { openSourceActivity(null)})
    }

    fun openSourceActivity(sources: List<Source>?) {

        rotateloading.stop()
        val intent = Intent(this, SourceActivity::class.java)

        sources?.let {
            intent.putParcelableArrayListExtra(IntentIdentifier.SOURCE_LIST, it as ArrayList<Source>)
        }

        startActivity(intent)
        finish()
    }

}
