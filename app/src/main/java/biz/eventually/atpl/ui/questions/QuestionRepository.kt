package biz.eventually.atpl.ui.questions

import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.dao.QuestionDao
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Question
import biz.eventually.atpl.ui.BaseRepository
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class QuestionRepository @Inject constructor(private val dataProvider: DataProvider, private val dao: QuestionDao) : BaseRepository() {


//    @AddTrace(name = "getSubjects", enabled = true)
//    fun getSubjects(sourceId: Long): LiveData<List<SubjectView>> {
//
//        if (hasInternetConnection()) getWebData(sourceId)
//        return dao.findBySourceId(sourceId)
//    }

    @AddTrace(name = "launchTest", enabled = true)
    fun getQuestions(topicId: Long, starFist: Boolean, then: (data: List<Question>) -> Unit) {

        // has Network: request data
        if (hasInternetConnection()) {
            getWebData(topicId, starFist, {
                then(getDataFromDb(topicId))
            })
        }
        // No network: taking in database
        else {
            then(getDataFromDb(topicId))
        }
    }

    fun updateFocus(questionId: Long, care: Boolean, then: (state: Boolean?) -> Unit) {
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
        }
    }

    private fun getDataFromDb(topicId: Long) : List<Question> {
        return dao.findByTopicId(topicId).map { Question(
                it.question.idWeb,
                topicId,
                it.question.label,
                it.question.img,
                it.question.focus,
                it.question.good,
                it.question.wrong
        ).apply {
            answers = it.answers ?: listOf()
        } }
    }

    private fun getWebData(topicId: Long, starFist: Boolean, then: () -> Unit) {

        status.postValue(NetworkStatus.LOADING)
        dataProvider
                .dataGetTopicQuestions(topicId, starFist)
                .subscribeOn(scheduler.network)
                ?.observeOn(scheduler.main)
                ?.subscribe({ questionsWeb ->
                    analyseData(topicId, questionsWeb)
                    then()
                    status.postValue(NetworkStatus.SUCCESS)
                }, { e ->
                    status.postValue(NetworkStatus.ERROR)
                    Timber.d("launchTest -> WebData: " + e)
                })
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
                    it.good = qWeb.good
                    it.wrong = qWeb.wrong

                    dao.update(it)
                }
            }
            // New
            else {
                qWeb.topicId = topicId
                dao.insert(qWeb)
            }
        }

        // update time reference
        if (questionsWeb.isNotEmpty()) LastCall("${LastCall.TYPE_TOPIC}_$topicId", Date().time).update()
    }
}