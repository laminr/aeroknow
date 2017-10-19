package biz.eventually.atpl.data

import android.content.Context
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.data.model.Follow
import biz.eventually.atpl.data.model.Question
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.service.SourceService
import biz.eventually.atpl.utils.Prefields.PREF_TOKEN
import biz.eventually.atpl.utils.prefsGetString
import biz.eventually.atpl.utils.prefsGetValue
import io.reactivex.Observable
import io.realm.RealmList
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 21/03/17.
 */
@Singleton
class DataProvider @Inject constructor(private val sourceService: SourceService, val context: Context) {

    fun dataGetSources() : Observable<List<Source>> {
        val lastCall = 0L //LastCall().queryFirst({ query -> query.equalTo("idWeb", LastCall.TYPE_SOURCE) })?.updated
        return sourceService.loadSources(lastCall ?: 0).map { api -> toAppSources(api.data) }
    }

    fun dataGetSubjects(sourceId: Int) : Observable<List<Subject>> {
        val lastCall = 0L //LastCall().queryFirst({ query -> query.equalTo("idWeb", LastCall.TYPE_SUBJECT) })?.updated
        val token = prefsGetString(context , PREF_TOKEN) ?: ""
        return sourceService.loadSubjects(sourceId, lastCall ?: 0, token).map { api -> toAppSubjects(sourceId, api.data) }
    }

    fun dataGetTopicQuestions(topicId: Int, startFirst: Boolean) : Observable<List<Question>> {
        val lastCall = 0L // LastCall().queryFirst({ query -> query.equalTo("idWeb", "${LastCall.TYPE_TOPIC}_$topicId") })?.updated
        val token = prefsGetValue(PREF_TOKEN, "")
        val questions = when (startFirst) {
            true -> sourceService.loadQuestionsStarred(topicId, lastCall ?: 0, token)
            false -> sourceService.loadQuestions(topicId, lastCall ?: 0, token)
        }

        return questions.map { response ->
            //response.data?.let(::toAppTopic) ?: Topic(-1, "", 0, null, null )
            response.data?.questions?.let(::toAppQuestions) ?: listOf()
        }
    }

    fun updateFollow(questionId: Int, good: Boolean) : Observable<Question?> {
        val isGood = if (good) 1 else 0
        val token = prefsGetValue( PREF_TOKEN, "")
        return sourceService.updateFollow(questionId, isGood , token).map { response ->
            response.data?.let(::toAppQuestion) ?: Question(-1, "", RealmList(), null, null, Follow() )
        }
    }

    fun updateFocus(questionId: Int, care: Boolean) : Observable<Int> {
        val doCare = if (care) 1 else 0
        val token = prefsGetValue( PREF_TOKEN, "")
        return sourceService.updateFocus(questionId, doCare , token).map { response ->
            response.data?.let({ it.focus?.let { if (it) 1 else 0 } }) ?: -1
        }
    }
}
