package biz.eventually.atpl.ui.subject

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.common.IntentIdentifier.Companion.REFRESH_SUBJECT
import biz.eventually.atpl.data.model.Question
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.questions.QuestionsActivity
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.firebase.perf.metrics.AddTrace
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.querySorted
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_subject.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SubjectActivity : BaseActivity<SubjectManager>() {

    companion object {
        val TAG = "SubjectActivity"
    }

    private var mSourceId: Int = 0
    private var mSubjectList: List<Subject>? = null

    private var mAdapter: SubjectAdapter = SubjectAdapter(this::onItemClick)

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
        subject_subject_list.layoutManager = mLayoutManager
        subject_subject_list.itemAnimator = DefaultItemAnimator()
        subject_subject_list.adapter = mAdapter

        subject_refresh.setOnClickListener {
            loadData()
        }
    }

    private fun onItemClick(topic: Topic, startFirst: Boolean = false): Unit {

        // Fabric Answer
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Subject")
                .putContentType("Questions")
                .putContentId("Source_${mSourceId}_subject_${topic.id}")
                .putCustomAttribute("Subject Name", "${topic.id}: ${topic.name}")
        )

        val intent = Intent(this, QuestionsActivity::class.java)

        intent.putExtra(IntentIdentifier.TOPIC, topic.id)
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

    override fun onResume() {
        super.onResume()
        gatherWhoHasOfflineData()
    }

    private fun displaySubjects(subjects: List<Subject>?) : Unit {
        mSubjectList = subjects
        mSubjectList?.let {
            val topics = mutableListOf<Topic>()

            it.forEach { t ->
                // header
                topics.add(Topic(-1, t.name))

                // line of topics
                topics.addAll(t.topics)
            }

            mAdapter.bind(topics)
            mAdapter.notifyDataSetChanged()
            rotateloading.stop()

            gatherWhoHasOfflineData()
        }
    }

    @AddTrace(name = "loadDataSubject", enabled = true)
    private fun loadData(silent: Boolean = false) {
        showHideError(View.GONE)
        if (!silent) rotateloading.start()

        if (mSourceId > 0) {
            manager.getSubjects(mSourceId, this::displaySubjects, this::onError)
        }
    }

    private fun gatherWhoHasOfflineData() {
        doAsync {
            uiThread {
                val topicIds = Question().querySorted("topicId", Sort.ASCENDING).groupBy { it.topicId }
                mAdapter.getBindedList().forEachIndexed { index, topic ->
                    if (topic.idWeb !in topicIds.keys)  {
                        topic.hasOfflineData = false
                        mAdapter.notifyItemChanged(index)
                    }
                }
            }
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
