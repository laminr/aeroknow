package biz.eventually.atpl.ui.subject

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.common.IntentIdentifier.Companion.REFRESH_SUBJECT
import biz.eventually.atpl.data.db.Focus
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.data.model.dto.TopicDto
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.questions.QuestionsActivity
import com.vicpin.krealmextensions.query
import kotlinx.android.synthetic.main.activity_subject.*

class SubjectActivity : BaseActivity<SubjectManager>() {

    companion object {
        val TAG = "SubjectActivity"
    }

    private var mSourceId: Int = 0
    private var mSubjectList: List<Subject>? = null

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

        when(mSubjectList) {
            null -> loadData()
            else -> displaySubjects(mSubjectList)
        }

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mAdapter = SubjectAdapter(this::onItemClick)
        subject_subject_list.layoutManager = mLayoutManager
        subject_subject_list.itemAnimator = DefaultItemAnimator()
        subject_subject_list.adapter = mAdapter

        subject_refresh.setOnClickListener {
            loadData()
        }
    }

    private fun onItemClick(dto: TopicDto, startFirst: Boolean = false): Unit {

        val intent = Intent(this, QuestionsActivity::class.java)
        val topic = Topic(dto.id, dto.name, listOf(), 0, 0)
        intent.putExtra(IntentIdentifier.TOPIC, topic)
        intent.putExtra(IntentIdentifier.TOPIC_STARRED, startFirst)

        startActivityForResult(intent, REFRESH_SUBJECT)

    }

    // to avoid source reloading
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REFRESH_SUBJECT && resultCode == RESULT_OK){
            loadData(true)
        }
    }

    fun displaySubjects(subjects: List<Subject>?) : Unit {
        mSubjectList = subjects
        mSubjectList?.let {
            val topics = mutableListOf<TopicDto>()

            it.forEach { (_, name, topic) ->
                // header
                topics.add(TopicDto(-1, name, 0, 0, 0, 0.0))

                //topic.forEach { t ->
                    //val focus = Focus().query { query -> query.equalTo("topicId", t.id).equalTo("care", true) }.count()
                    //t.focus = if (focus > t.focus) focus else t.focus
                //}
                // line of topics
                topics.addAll(topic)
            }

            mAdapter?.bind(topics)
            mAdapter?.notifyDataSetChanged()
            rotateloading.stop()
        }
    }

    private fun loadData(silent: Boolean = false) {
        showHideError(View.GONE)
        if (!silent) rotateloading.start()

        if (mSourceId > 0) {
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
