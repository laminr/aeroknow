package biz.eventually.atpl.ui.questions

import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.dao.LastCallDao
import biz.eventually.atpl.data.dao.QuestionDao
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Question
import biz.eventually.atpl.ui.BaseRepository
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 *
 */
@Singleton
class QuestionRepository @Inject constructor(private val dataProvider: DataProvider, private val dao: QuestionDao, private val lastCallDao: LastCallDao) : BaseRepository() {

    @AddTrace(name = "launchTest", enabled = true)
    fun getQuestions(topicId: Long, starFist: Boolean, then: (data: List<Question>) -> Unit) {

        // has Network: request data
        if (hasInternetConnection()) {
            getWebData(topicId, starFist) { data ->
                then(data)
            }
        }
        // No network: taking in database
        else {
            then(getDataFromDb(topicId))
        }
    }

    fun getWebData(topicId: Long, starFist: Boolean, silent: Boolean = false, then: (data: List<Question>) -> Unit) {

        if (!silent) status.postValue(NetworkStatus.LOADING)
        dataProvider
                .dataGetTopicQuestions(topicId, starFist)
                .subscribeOn(scheduler.network)
                .map { question -> analyseData(topicId, question) }
                .map { getDataFromDb(topicId) }
                .observeOn(scheduler.main)
                .subscribe({ data ->
                    if (!silent)  status.postValue(NetworkStatus.SUCCESS)
                    then(data)
                }, { e ->
                    if (!silent)  status.postValue(NetworkStatus.ERROR)
                    Timber.d("launchTest -> WebData: " + e)
                })
    }


    fun updateFocus(questionId: Long, care: Boolean, then: (state: Boolean?) -> Unit) {
        if (hasInternetConnection()) {
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
                    }, { e ->
                        Timber.d("Question -> updateFocus: " + e)
                        then(null)
                    })
        } else {
            doAsync {
                dao.findById(questionId)?.let {
                    it.focus = if (it.focus == care) null else care

                    dao.update(it)
                    then(it.focus)
                }
            }
        }
    }

    fun updateFollow(questionId: Long, good: Boolean, then: (question: Question?) -> Unit) {
        if (hasInternetConnection()) {
            dataProvider.updateFollow(questionId, good)
                    .subscribeOn(scheduler.network)
                    ?.observeOn(scheduler.main)
                    ?.subscribe({ question ->
                        dao.findById(question.idWeb)?.let {
                            it.good = question.good
                            it.wrong = question.wrong

                            dao.update(it)
                        }
                        then(question)
                    }, { e ->
                        Timber.d("Question -> updateFollow: " + e)
                        then(null)
                    })
        } else {
            doAsync {
                dao.findById(questionId)?.let {
                    when (good) {
                        true -> it.good += 1
                        false -> it.wrong += 1
                    }

                    dao.update(it)
                    then(it)
                }
            }
        }
    }

    private fun getDataFromDb(topicId: Long): List<Question> {
        return dao.findByTopicId(topicId).map {
            Question(
                    it.question.idWeb,
                    topicId,
                    it.question.label,
                    it.question.img,
                    it.question.focus,
                    it.question.good,
                    it.question.wrong
            ).apply {
                answers = it.answers ?: listOf()
            }
        }
    }


    private fun analyseData(topicId: Long, questionsWeb: List<Question>) {

        val questionsId = dao.getIds()

        questionsWeb.forEach { qWeb ->
            // Update
            if (qWeb.idWeb in questionsId) {
                dao.findById(qWeb.idWeb)?.let {
                    it.label = qWeb.label
                    it.img = qWeb.img
                    it.focus = qWeb.focus
                    it.good = qWeb.good
                    it.wrong = qWeb.wrong

                    dao.updateQuestionAndAnswers(it)
                }
            }
            // New
            else {
                qWeb.topicId = topicId
                dao.insertQuestionAndAnswers(qWeb)
            }
        }

        // update time reference
        if (questionsWeb.isNotEmpty()) {
            lastCallDao.updateOrInsert(LastCall("${LastCall.TYPE_TOPIC}_$topicId", Date().time))
        }
    }
}