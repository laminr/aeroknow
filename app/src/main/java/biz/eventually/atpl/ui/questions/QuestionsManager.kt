package biz.eventually.atpl.ui.source

import android.util.Log
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.model.Question
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import com.vicpin.krealmextensions.saveManaged
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class QuestionsManager @Inject constructor(private val dataProvider: DataProvider) : RxBaseManager() {

    companion object {
        val TAG = "QuestionsManager"
    }

    @AddTrace(name = "getQuestions", enabled = true)
    fun getQuestions(topicId: Long, starFist: Boolean, display: (qs: List<Question>) -> Unit, error: () -> Unit) {

        val questionsDb = Question().query({ s -> s.equalTo("topicId", topicId) }).toMutableList()

        if (hasInternetConnection()) {
            dataProvider
                    .dataGetTopicQuestions(topicId, starFist)
                    .subscribeOn(scheduler.network)
                    ?.observeOn(scheduler.main)
                    ?.subscribe({ questionsWeb ->
                        analyseData(topicId, questionsDb, questionsWeb)
                        display(questionsDb)
                    }, { _ ->
                        error()
                        Log.d(TAG, "getQuestions: " + error)
                    })
        } else {
            display(questionsDb)
        }
    }

    fun updateFocus(questionId: Int, care: Boolean, then: (state: Boolean?) -> Unit, error: () -> Unit) {
        dataProvider.updateFocus(questionId, care)
                .subscribeOn(scheduler.network)
                ?.observeOn(scheduler.main)
                ?.subscribe({ focusInt ->
                    val focus = when (focusInt) {
                        0 -> false
                        1 -> true
                        else -> null
                    }
                    var question = Question().queryFirst({ s -> s.equalTo("idWeb", questionId) })
                    question?.let {
                        it.focus = focus
                        it.save()
                    }
                    then(focus)
                }, { _ ->
                    Log.d(TAG, "updateFocus: " + error)
                    error()
                })
    }

    fun updateFollow(questionId: Int, good: Boolean) {
        if (hasInternetConnection()) {
            dataProvider.updateFollow(questionId, good)
                    .subscribeOn(scheduler.network)
                    ?.observeOn(scheduler.main)
                    ?.subscribe({ question ->
                        question?.let {
                            if (it.idWeb != -1) {
                                it.save()
                            }
                        }
                    }, { error ->
                        Log.d(TAG, "updateFollow: " + error)
                    })
        }
    }

    private fun analyseData(topicId: Long, questionsDb: MutableList<Question>, questionsWeb: List<Question>) {

        val questionsId = questionsDb.map { it.idWeb }

        questionsWeb.forEach { qWeb ->
            // Update
            if (qWeb.idWeb in questionsId) {
                Question().queryFirst({ query -> query.equalTo("idWeb", qWeb.idWeb) })
                        ?.let {
                            it.label = qWeb.label
                            it.answers = qWeb.answers
                            it.img = qWeb.img
                            it.focus = qWeb.focus
                            it.follow = qWeb.follow

                            it.save()
                        }
            }
            // New
            else {
                qWeb.topicId = topicId.toInt()
                qWeb.save()
                questionsDb.add(qWeb)
            }
        }

        // update time reference
        if (questionsWeb.isNotEmpty()) LastCall().update("${LastCall.TYPE_TOPIC}_$topicId", Date().time)
    }
}


