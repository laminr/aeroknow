package biz.eventually.atpl.data.service

import biz.eventually.atpl.data.network.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by thibault on 21/03/17.
 */
interface SourceService {

    @GET("source")
    fun loadSources(): Observable<ApiResponse<SourceNetwork>>

    @GET("source/{id}")
    fun loadSubjects(@Path("id") sourceId: Int, @Query("token") token: String): Observable<ApiResponse<SubjectNetwork>>

    @GET("topic/{id}/full")
    fun loadQuestions(@Path("id") topicId: Int, @Query("token") token: String): Observable<ApiSingleResponse<TopicNetwork>>

    @GET("topic/{id}/full/star")
    fun loadQuestionsStarred(@Path("id") topicId: Int, @Query("token") token: String): Observable<ApiSingleResponse<TopicNetwork>>

    @GET("question/{id}/focus/{care}")
    fun updateFocus(@Path("id") questionId: Int, @Path("care") care: Int, @Query("token") token: String): Observable<ApiSingleResponse<FocusStateNetwork>>

    @GET("question/{id}/follow/{good}")
    fun updateFollow(@Path("id") v: Int, @Path("good") good: Int, @Query("token") token: String): Observable<ApiSingleResponse<QuestionNetwork>>
}
