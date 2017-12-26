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
import android.view.View.GONE
import android.view.View.VISIBLE
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.common.IntentIdentifier.Companion.REFRESH_SUBJECT
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.db.Topic
import biz.eventually.atpl.data.dto.SubjectView
import biz.eventually.atpl.data.dto.TopicView
import biz.eventually.atpl.ui.BaseComponentActivity
import biz.eventually.atpl.ui.ViewModelFactory
import biz.eventually.atpl.ui.questions.QuestionRepository
import biz.eventually.atpl.ui.questions.QuestionViewModel
import biz.eventually.atpl.ui.questions.QuestionsActivity
import biz.eventually.atpl.utils.hasInternetConnection
import cn.pedant.SweetAlert.SweetAlertDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.firebase.perf.metrics.AddTrace
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class SubjectActivity : BaseComponentActivity() {

    private var mTopicViewList: List<TopicView> = listOf()
    private var mSourceId: Long = 0

    @Inject
    lateinit var subjectViewModelFactory: ViewModelFactory<SubjectRepository>

    @Inject
    lateinit var questionViewModelFactory: ViewModelFactory<QuestionRepository>

    private lateinit var mViewModel: SubjectViewModel
    private lateinit var mQViewModel: QuestionViewModel

    private var mAdapter: SubjectAdapter = SubjectAdapter(this::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        AtplApplication.component.inject(this)

        mViewModel = ViewModelProviders.of(this, subjectViewModelFactory).get(SubjectViewModel::class.java)
        mQViewModel = ViewModelProviders.of(this, questionViewModelFactory).get(QuestionViewModel::class.java)

        Fabric.with(this, Answers())

        setContentView(R.layout.activity_subject)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sourceName = intent.extras.getString(IntentIdentifier.SOURCE_NAME)
        supportActionBar?.title = sourceName ?: ""

        mSourceId = intent.extras.getLong(IntentIdentifier.SOURCE_ID)

        mViewModel.setSourceId(mSourceId)
        mViewModel.subjects.observe(this, Observer<List<SubjectView>> {
            doAsync {
                val data = transform(it)
                uiThread {
                    displaySubjects(data)
                }
            }
        })

        mViewModel.networkStatus.observe(this, Observer<NetworkStatus> {
            when (it) {
                NetworkStatus.LOADING -> {
                    subject_refresh.visibility = GONE
                    subject_rotate.start()
                }
                else -> {
                    subject_refresh.visibility = VISIBLE
                    subject_rotate.stop()
                }
            }
        })

        mQViewModel.updateLine.observe(this, Observer<Triple<Long, Boolean, Boolean>> { it ->
            it?.let {
                val (idWeb, isSync, hasOffline) = it
                updateTopicLine(idWeb, isSync, hasOffline)
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

    private fun transform(subjects: List<SubjectView>?): List<TopicView> {

        val topics = mutableListOf<TopicView>()

        val ids = mViewModel.getTopicIdWithQuestion()

        subjects?.forEach { t ->
            // header
            val titleTopic = Topic((t.subject.idWeb * -1), t.subject.idWeb, t.subject.name)
            topics.add(TopicView(titleTopic))

            // line of topics
            topics.addAll(t.topics.map {
                TopicView(it, hasOfflineData = it.idWeb in ids)
            })
        }


        return topics
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

            mViewModel.subjects.value?.let {
                mQViewModel.getDataForSubject(topic.idWeb, it)
            }
        }
        // display questions for subject
        else {
            var openActivity = true

            if (!hasInternetConnection()) {
                if (topic.questions == 0) {
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
        } else {
            mViewModel.refresh(mSourceId)
        }
    }

    private fun displaySubjects(topics: List<TopicView>) {
        mTopicViewList = topics
        mAdapter.bind(mTopicViewList)
        mAdapter.notifyDataSetChanged()

        showHideError(if (mTopicViewList.isEmpty()) View.VISIBLE else View.GONE)
    }

    @AddTrace(name = "loadDataSubject", enabled = true)
    private fun loadData(silent: Boolean = false) {
        if (mSourceId > 0) {
            mViewModel.setSourceId(mSourceId, silent)
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

    private fun showHideError(show: Int) {
        subject_error.visibility = show
        subject_refresh.visibility = show
    }

    private fun onError() {
        showHideError(View.VISIBLE)
    }
}
