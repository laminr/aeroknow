package biz.eventually.atpl.ui.subject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import biz.eventually.atpl.AtplApplication

import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.network.model.Subject
import biz.eventually.atpl.ui.source.SourceAdapter
import javax.inject.Inject

import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.content_subject.*
import android.support.v7.widget.DefaultItemAnimator
import biz.eventually.atpl.network.model.Topic

class SubjectActivity : AppCompatActivity() {

    companion object {
        val TAG = "SubjectActivity"
    }

    @Inject lateinit var manager: SubjectManager

    private var sourceId: Int = 0
    private var mAdapter: SubjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AtplApplication.component.inject(this)

        setContentView(R.layout.activity_subject)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sourceName = intent.extras.getString(IntentIdentifier.SOURCE_NAME)
        toolbar.title = sourceName

        sourceId = intent.extras.getInt(IntentIdentifier.SOURCE_ID)
        if (sourceId > 0) {
            manager.getSubjects(sourceId, this::displaySubjects)
        }

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mAdapter = SubjectAdapter()
        subject_subject_list.layoutManager = mLayoutManager
        subject_subject_list.itemAnimator = DefaultItemAnimator()
        subject_subject_list.adapter = mAdapter
    }

    fun displaySubjects(subjects: List<Subject>?) : Unit {
        subjects?.let {
            val topics = mutableListOf<Topic>()

            subjects.forEach { s ->
                topics.add(Topic(-1, s.name, 0, 0, 0))
                topics.addAll(s.topics)
            }

            mAdapter?.bind(topics)
            mAdapter?.notifyDataSetChanged()
        }
    }
}
