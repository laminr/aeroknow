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
import biz.eventually.atpl.data.dto.TopicDto
import biz.eventually.atpl.data.model.Question
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.questions.QuestionsActivity
import biz.eventually.atpl.ui.source.QuestionsManager
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.firebase.perf.metrics.AddTrace
import com.vicpin.krealmextensions.querySorted
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_subject.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class SubjectActivity : BaseActivity<SubjectManager>() {

    companion object {
        val TAG = "SubjectActivity"
    }

    @Inject
    lateinit var questionManager: QuestionsManager

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

    private fun onItemClick(topic: Topic, startFirst: Boolean = false) {

        // Subject
        if (topic.idWeb < 0) {
            // Fabric Answer - Download data
            Answers.getInstance().logContentView(ContentViewEvent()
                    .putContentName("Subject")
                    .putContentType("Download Questions")
                    .putContentId("Source_${mSourceId}_subject_${topic.id}")
                    .putCustomAttribute("Download offline", "${topic.id}: ${topic.name}")
            )

            doAsync {
                mSubjectList?.forEach {
                    // here the topic is in fact a Subject, w/ idWeb = id * -1
                    if (it.idWeb == (topic.idWeb * -1)) {
                        it.topics.forEach { topic ->
                            updateTopicLine(topic.idWeb, true)
                            questionManager.getQuestions(topic.idWeb, false, fun(_: List<Question>) {
                                updateTopicLine(topic.idWeb, hasOffline = true)
                            }, {})
                        }
                    }
                }
            }
        }
        // display questions for subject
        else {
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

    override fun onRestart() {
        super.onRestart()
        gatherWhoHasOfflineData()
    }

    private fun displaySubjects(subjects: List<Subject>?) {
        mSubjectList = subjects
        mSubjectList?.let {
            val topics = mutableListOf<TopicDto>()

            it.forEach { t ->
                // header
                val titleTopic = Topic((t.idWeb * -1), t.name)
                topics.add(TopicDto(titleTopic, false, false))

                // line of topics
                topics.addAll(t.topics.map { TopicDto(it, false, false) })
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

    private fun updateTopicLine(topicId: Int, isSync: Boolean = false, hasOffline: Boolean = false) {
        doAsync {
            uiThread {
                mAdapter.getBindedList().forEachIndexed { index, topicDto ->
                    if (topicDto.topic.idWeb == topicId)  {
                        topicDto.isSync = isSync
                        topicDto.hasOfflineData = hasOffline
                        mAdapter.notifyItemChanged(index)
                    }
                }
            }
        }
    }

    private fun gatherWhoHasOfflineData() {
        doAsync {
            uiThread {
                val topicIds = Question().querySorted("topicId", Sort.ASCENDING).groupBy { it.topicId }
                mAdapter.getBindedList().forEachIndexed { index, topicDto ->
                    val doesHasOffline = topicDto.topic.idWeb in topicIds.keys
                    if (doesHasOffline != topicDto.hasOfflineData)  {
                        topicDto.hasOfflineData = doesHasOffline
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

    private fun onError() {
        rotateloading.stop()
        showHideError(View.VISIBLE)
    }
}
