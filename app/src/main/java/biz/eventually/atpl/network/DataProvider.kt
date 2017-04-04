package biz.eventually.atpl.network

import biz.eventually.atpl.network.model.Source
import biz.eventually.atpl.network.model.Subject
import biz.eventually.atpl.network.model.Topic
import biz.eventually.atpl.network.network.Question
import biz.eventually.atpl.network.network.QuestionNetwork
import biz.eventually.atpl.network.toAppSources
import biz.eventually.atpl.network.service.SourceService
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
