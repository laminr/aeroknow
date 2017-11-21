package biz.eventually.atpl.ui.questions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.dao.QuestionDao
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Question
import biz.eventually.atpl.data.dto.QuestionView
import biz.eventually.atpl.ui.source.QuestionsManager
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class QuestionRepository @Inject constructor(private val dataProvider: DataProvider, private val dao: QuestionDao) : RxBaseManager() {

    private var status: MutableLiveData<NetworkStatus> = MutableLiveData()

    fun networkStatus(): LiveData<NetworkStatus> = status

//    @AddTrace(name = "getSubjects", enabled = true)
//    fun getSubjects(sourceId: Long): LiveData<List<SubjectView>> {
//
//        if (hasInternetConnection()) getWebData(sourceId)
//        return dao.findBySourceId(sourceId)
//    }

    @AddTrace(name = "getQuestions", enabled = true)
    fun getQuestions(topicId: Long, starFist: Boolean): LiveData<List<QuestionView>> {

        if (hasInternetConnection()) getWebData(topicId, starFist)
        return dao.findByTopicId(topicId)
    }

    private fun getWebData(topicId: Long, starFist: Boolean) {

        dataProvider
                .dataGetTopicQuestions(topicId, starFist)
                .subscribeOn(scheduler.network)
                ?.observeOn(scheduler.main)
                ?.subscribe({ questionsWeb ->
                    analyseData(topicId, questionsWeb)
                }, { _ ->
//                    error()
                    Log.d(QuestionsManager.TAG, "getQuestions: ")
                })
    }

    fun updateFocus(questionId: Long, care: Boolean, then: (state: Boolean?) -> Unit, error: () -> Unit) {
        dataProvider.updateFocus(questionId, care)
                .subscribeOn(scheduler.network)
                ?.observeOn(scheduler.main)
                ?.subscribe({ focusInt ->
                    val focus = when (focusInt) {
                        0 -> false
                        1 -> true
                        else -> null
                    }

                    dao.findById(questionId)?.let {
                        it.focus = focus
                        dao.update(it)
                    }

                    then(focus)
                }, { _ ->
                    Log.d(QuestionsManager.TAG, "updateFocus: " + error)
                    error()
                })
    }

    fun updateFollow(questionId: Long, good: Boolean) {
        if (hasInternetConnection()) {
            dataProvider.updateFollow(questionId, good)
                    .subscribeOn(scheduler.network)
                    ?.observeOn(scheduler.main)
                    ?.subscribe({ question ->
                        question?.let {
                            if (it.idWeb != -1L) {
                                dao.insert(it)
                            }
                        }
                    }, { error ->
                        Log.d(QuestionsManager.TAG, "updateFollow: " + error)
                    })
        }
    }

    private fun analyseData(topicId: Long, questionsWeb: List<Question>) {

        val questionsId = dao.getIds()

        questionsWeb.forEach { qWeb ->
            // Update
            if (qWeb.idWeb in questionsId) {
                dao.findById(qWeb.idWeb)?.let {
                    it.label = qWeb.label
                    it.answers = qWeb.answers
                    it.img = qWeb.img
                    it.focus = qWeb.focus
                    it.follow = qWeb.follow

                    dao.update(it)
                }
            }
            // New
            else {
                qWeb.topicId = topicId.toInt()
                dao.insert(qWeb)
            }
        }

        // update time reference
        if (questionsWeb.isNotEmpty()) LastCall().update("${LastCall.TYPE_TOPIC}_$topicId", Date().time)
    }
}