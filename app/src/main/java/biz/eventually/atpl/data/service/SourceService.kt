package biz.eventually.atpl.data.service

import biz.eventually.atpl.data.network.QuestionNetwork
import biz.eventually.atpl.data.network.SourceNetwork
import biz.eventually.atpl.data.network.SubjectNetwork
import biz.eventually.atpl.data.network.TopicNetwork
import retrofit2.http.GET
import io.reactivex.Observable
import retrofit2.http.Path

/**
 * Created by thibault on 21/03/17.
 */
interface SourceService {

    @GET("source")
    fun loadSources(): Observable<ApiResponse<SourceNetwork>>

    @GET("source/{id}")
    fun loadSubjects(@Path("id") sourceId: Int): Observable<ApiResponse<SubjectNetwork>>

    @GET("topic/{id}/full")
    fun loadQuestions(@Path("id") topicId: Int): Observable<ApiSingleResponse<TopicNetwork>>
}
