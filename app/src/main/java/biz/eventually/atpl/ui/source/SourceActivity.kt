package biz.eventually.atpl.ui.source

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.network.model.Source
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.subject.SubjectActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_source.*

class SourceActivity : BaseActivity<SourceManager>() {

    private var mAdapter: SourceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AtplApplication.component.inject(this)
        setContentView(R.layout.activity_source)

        source_error.visibility = GONE
        manager.getSources(this::displayData, this::onError)
        rotateloading.start()
    }

    private fun displayData(sources: List<Source>?): Unit {


        sources?.apply {
            mAdapter = SourceAdapter(this@SourceActivity, sources)

            source_listview.adapter = mAdapter
            source_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

                val intent = Intent(this@SourceActivity, SubjectActivity::class.java)
                intent.putExtra(IntentIdentifier.SOURCE_ID, get(position).id)
                intent.putExtra(IntentIdentifier.SOURCE_NAME, get(position).name)
                startActivity(intent)
            }
            rotateloading.stop()
        }
    }

    private fun onError(): Unit {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.error_dialog_title))
                .setContentText(getString(R.string.error_dialog_message))
                .show()

        rotateloading.stop()
        source_error.visibility = VISIBLE
    }
}
