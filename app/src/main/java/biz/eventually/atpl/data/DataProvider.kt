package biz.eventually.atpl.data

import android.content.Context
import biz.eventually.atpl.data.model.Source
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.data.network.Question
import biz.eventually.atpl.data.service.SourceService
import biz.eventually.atpl.utils.PREF_TOKEN
import biz.eventually.atpl.utils.PrefsGetString
import io.reactivex.Observable

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 21/03/17.
 */
@Singleton
class DataProvider @Inject constructor(private val sourceService: SourceService, val context: Context) {

    fun dataGetSources() : Observable<List<Source>?>? {
        return sourceService.loadSources().map { api -> toAppSources(api.data) }
    }

    fun dataGetSubjects(sourceId: Int) : Observable<List<Subject>?>? {
        val token = PrefsGetString(context , PREF_TOKEN) ?: ""
        return sourceService.loadSubjects(sourceId, token).map { api -> toAppSubjects(api.data) }
    }

    fun dataGetTopicQuestions(topicId: Int, startFirst: Boolean) : Observable<Topic>? {
        val token = PrefsGetString(context , PREF_TOKEN) ?: ""
        val questions = when (startFirst) {
            true -> sourceService.loadQuestionsStarred(topicId, token)
            false -> sourceService.loadQuestions(topicId, token)
        }

        return questions.map { response ->
            response.data?.let(::toAppTopic)
        }
    }

    fun updateFollow(questionId: Int, good: Boolean) : Observable<Question>? {
        val isGood = if (good) 1 else 0
        val token = PrefsGetString(context , PREF_TOKEN) ?: ""
        return sourceService.updateFollow(questionId, isGood , token).map { response ->
            response.data?.let(::toAppQuestion)
        }
    }

    fun updateFocus(questionId: Int, care: Boolean) : Observable<Int>? {
        val doCare = if (care) 1 else 0
        val token = PrefsGetString(context , PREF_TOKEN) ?: ""
        return sourceService.updateFocus(questionId, doCare , token).map { response ->
            response.data?.let({ it.focus?.let { if (it) 1 else 0 } }) ?: -1
        }
    }
}
