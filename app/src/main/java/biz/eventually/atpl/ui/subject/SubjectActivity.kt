package biz.eventually.atpl.ui.subject

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.dto.TopicDto
import biz.eventually.atpl.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.content_subject.*

class SubjectActivity : BaseActivity<SubjectManager>() {

    companion object {
        val TAG = "SubjectActivity"
    }

    private var mSourceId: Int = 0
    private var mAdapter: SubjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AtplApplication.component.inject(this)

        setContentView(R.layout.activity_subject)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sourceName = intent.extras.getString(IntentIdentifier.SOURCE_NAME)
        supportActionBar?.title = sourceName

        mSourceId = intent.extras.getInt(IntentIdentifier.SOURCE_ID)
        loadData()

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mAdapter = SubjectAdapter()
        subject_subject_list.layoutManager = mLayoutManager
        subject_subject_list.itemAnimator = DefaultItemAnimator()
        subject_subject_list.adapter = mAdapter

        subject_refresh.setOnClickListener {
            loadData()
        }
    }

    fun displaySubjects(subjects: List<Subject>?) : Unit {
        subjects?.let {
            val topics = mutableListOf<TopicDto>()

            subjects.forEach { s ->
                topics.add(TopicDto(-1, s.name, 0, 0, 0))
                topics.addAll(s.topics)
            }

            mAdapter?.bind(topics)
            mAdapter?.notifyDataSetChanged()
            rotateloading.stop()
        }
    }

    private fun loadData() {
        showHideError(View.GONE)
        rotateloading.start()
        if (mSourceId > 0) {
            rotateloading.start()
            manager.getSubjects(mSourceId, this::displaySubjects, this::onError)
        }
    }

    private fun showHideError(show: Int) {
        subject_error.visibility = show
        subject_refresh.visibility = show
    }

    private fun onError(): Unit {
        rotateloading.stop()
        showHideError(View.VISIBLE)
    }
}
