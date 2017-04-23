package biz.eventually.atpl.data

import biz.eventually.atpl.data.model.Source
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.data.network.Question
import biz.eventually.atpl.data.network.QuestionNetwork
import biz.eventually.atpl.data.toAppSources
import biz.eventually.atpl.data.service.SourceService
import io.reactivex.Observable

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 21/03/17.
 */
@Singleton
class DataProvider @Inject constructor(private val sourceService: SourceService) {

    fun dataGetSources() : Observable<List<Source>?>? {
        return sourceService.loadSources().map { api -> toAppSources(api.data) }
    }

    fun dataGetSubjects(sourceId: Int) : Observable<List<Subject>?>? {
        return sourceService.loadSubjects(sourceId).map { api -> toAppSubjects(api.data) }
    }

    fun dataGetTopicQuestions(topicId: Int) : Observable<Topic>? {
        return sourceService.loadQuestions(topicId).map { response ->
            response.data?.let(::toAppTopic)
        }
    }
}
