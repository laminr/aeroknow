package biz.eventually.atpl.data

import android.content.Context
import biz.eventually.atpl.data.dao.LastCallDao
import biz.eventually.atpl.data.db.Question
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.service.SourceService
import biz.eventually.atpl.utils.Prefields.PREF_TOKEN
import biz.eventually.atpl.utils.prefsGetString
import biz.eventually.atpl.utils.prefsGetValue
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 21/03/17.
 *
 */
@Singleton
class DataProvider @Inject constructor(private val sourceService: SourceService, val context: Context, val lastCallDao: LastCallDao) {

    fun dataGetSources(lastCall: Long = 0L): Observable<List<Source>> {
        return sourceService.loadSources(lastCall).map { api -> toAppSources(api.data) }
    }

    fun dataGetSubjects(sourceId: Long, lastCall: Long = 0L): Observable<List<Subject>> {
        return sourceService.loadSubjects(sourceId, lastCall).map { api -> toAppSubjects(sourceId, api.data) }
    }

    fun dataGetTopicQuestions(topicId: Long, startFirst: Boolean, lastCall: Long = 0L): Observable<List<Question>> {
        val questions = when (startFirst) {
            true -> sourceService.loadQuestionsStarred(topicId, lastCall)
            false -> sourceService.loadQuestions(topicId, lastCall)
        }

        return questions.map { response ->
            response.data?.questions?.let { toAppQuestions(topicId, it) } ?: listOf()
        }
    }
}
