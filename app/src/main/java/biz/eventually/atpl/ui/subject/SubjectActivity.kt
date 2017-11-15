package biz.eventually.atpl.ui.subject

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.db.Topic
import biz.eventually.atpl.data.dto.SubjectView
import biz.eventually.atpl.data.dto.TopicView
import biz.eventually.atpl.data.model.Question
import biz.eventually.atpl.ui.BaseComponentActivity
import biz.eventually.atpl.ui.ViewModelFactory
import biz.eventually.atpl.ui.questions.QuestionsActivity
import biz.eventually.atpl.ui.source.QuestionsManager
import biz.eventually.atpl.utils.hasInternetConnection
import cn.pedant.SweetAlert.SweetAlertDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.firebase.perf.metrics.AddTrace
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.querySorted
import io.fabric.sdk.android.Fabric
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class SubjectActivity : BaseComponentActivity() {

    @Inject
    lateinit var questionManager: QuestionsManager

    private var mSubjectList: List<SubjectView> = listOf()
    private var mSourceId: Long = 0

    @Inject
    lateinit var subjectViewModelFactory: ViewModelFactory<SubjectRepository>

    private lateinit var viewModel: SubjectViewModel

    private var mAdapter: SubjectAdapter = SubjectAdapter(this::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        AtplApplication.component.inject(this)

        viewModel = ViewModelProviders.of(this, subjectViewModelFactory).get(SubjectViewModel::class.java)

        Fabric.with(this, Answers())

        setContentView(R.layout.activity_subject)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sourceName = intent.extras.getString(IntentIdentifier.SOURCE_NAME)
        supportActionBar?.title = sourceName ?: ""

        mSourceId = intent.extras.getLong(IntentIdentifier.SOURCE_ID)

        viewModel.setSourceId(mSourceId)
        viewModel.subjects.observe(this, Observer<List<SubjectView>> {
            displaySubjects(it ?: listOf())
        })

        viewModel.networkStatus.observe(this, Observer<NetworkStatus> {
            when (it) {
                NetworkStatus.LOADING -> subject_rotate.start()
                else -> subject_rotate.stop()
            }
        })

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
                    .putContentId("Source_${mSourceId}_subject_${topic.idWeb}")
                    .putCustomAttribute("Download offline", "${topic.idWeb}: ${topic.name}")
            )

            mSubjectList.forEach {
                // here the topic is in fact a Subject, w/ idWeb = idWeb * -1
                if (it.subject.idWeb == (topic.idWeb * -1)) {

                    var count = 0
                    val subjectId = topic.idWeb
                    val max = it.topics.count()

                    it.topics.forEach { topic ->
                        updateTopicLine(topic.idWeb, true)
                        questionManager.getQuestions(topic.idWeb, false, fun(_: List<Question>) {
                            updateTopicLine(topic.idWeb, hasOffline = true)
                            if (++count == max) updateTopicLine(subjectId, false)
                        }, {
                            if (++count == max) updateTopicLine(subjectId, false)
                        })
                    }
                }
            }
        }
        // display questions for subject
        else {
            var openActivity = true

            if (!hasInternetConnection()) {
                if (Question().query({ query -> query.equalTo("topicId", topic.idWeb) }).count() == 0) {
                    openActivity = false

                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.msg_offline_title))
                            .setContentText(getString(R.string.error_offline_no_data))
                            .show()
                }
            }

            if (openActivity) {
                // Fabric Answer
                Answers.getInstance().logContentView(ContentViewEvent()
                        .putContentName("Subject")
                        .putContentType("Questions")
                        .putContentId("Source_${mSourceId}_subject_${topic.idWeb}")
                        .putCustomAttribute("Subject Name", "${topic.idWeb}: ${topic.name}")
                )

                val intent = Intent(this, QuestionsActivity::class.java)

                intent.putExtra(IntentIdentifier.TOPIC, topic.idWeb)
                intent.putExtra(IntentIdentifier.TOPIC_STARRED, startFirst)

                startActivityForResult(intent, REFRESH_SUBJECT)
            }
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

        if (requestCode == REFRESH_SUBJECT && resultCode == RESULT_OK) {
            loadData(true)
        }
    }

    override fun onRestart() {
        super.onRestart()
        gatherWhoHasOfflineData()
    }

    private fun displaySubjects(subjects: List<SubjectView>) {
        mSubjectList = subjects
        mSubjectList.let {
            val topics = mutableListOf<TopicView>()

            it.forEach { t ->
                // header
                val titleTopic = Topic((t.subject.idWeb * -1), t.subject.idWeb, t.subject.name)
                topics.add(TopicView(titleTopic))

                // line of topics
                topics.addAll(t.topics.map { TopicView(it) })
            }

            mAdapter.bind(topics)
            mAdapter.notifyDataSetChanged()

            gatherWhoHasOfflineData()
        }

        showHideError(if (mSubjectList.isEmpty()) View.VISIBLE else View.GONE)
    }

    @AddTrace(name = "loadDataSubject", enabled = true)
    private fun loadData(silent: Boolean = false) {
        // FIXME: Handle the silent param
//        if (!silent) subject_rotate.start()
        if (mSourceId > 0) {
            viewModel.setSourceId(mSourceId)
        } else {
            showHideError(R.string.dialog_title_error)
        }
    }

    private fun updateTopicLine(topicId: Long, isSync: Boolean = false, hasOffline: Boolean = false) {
        launch(UI) {
            mAdapter.getList().forEachIndexed { index, topicDto ->
                if (topicDto.topic.idWeb == topicId) {
                    topicDto.isSync = isSync
                    topicDto.hasOfflineData = hasOffline
                    mAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun gatherWhoHasOfflineData() {
        launch(UI) {
            val topicIds = Question().querySorted("topicId", Sort.ASCENDING).groupBy { it.topicId }
            mAdapter.getList().forEachIndexed { index, topicDto ->
                val doesHasOffline = topicDto.topic.idWeb.toInt() in topicIds.keys
                if (doesHasOffline != topicDto.hasOfflineData) {
                    topicDto.hasOfflineData = doesHasOffline
                    mAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun showHideError(show: Int) {
        subject_error.visibility = show
        subject_refresh.visibility = show
    }

    private fun onError() {
        showHideError(View.VISIBLE)
    }
}
