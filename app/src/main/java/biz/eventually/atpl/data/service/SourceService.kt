package biz.eventually.atpl.data.service

import biz.eventually.atpl.data.network.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by thibault on 21/03/17.
 */
interface SourceService {

    @GET("source")
    fun loadSources(@Query("last") lastCall: Long): Observable<ApiResponse<SourceNetwork>>

    @GET("source/{idWeb}")
    fun loadSubjects(@Path("idWeb") sourceId: Int, @Query("last") lastCall: Long, @Query("token") token: String): Observable<ApiResponse<SubjectNetwork>>

    @GET("topic/{idWeb}/full")
    fun loadQuestions(@Path("idWeb") topicId: Int, @Query("last") lastCall: Long, @Query("token") token: String): Observable<ApiSingleResponse<TopicWithQuestionNetwork>>

    @GET("topic/{idWeb}/full/star")
    fun loadQuestionsStarred(@Path("idWeb") topicId: Int, @Query("last") lastCall: Long, @Query("token") token: String): Observable<ApiSingleResponse<TopicWithQuestionNetwork>>

    @GET("question/{idWeb}/focus/{care}")
    fun updateFocus(@Path("idWeb") questionId: Int, @Path("care") care: Int, @Query("token") token: String): Observable<ApiSingleResponse<FocusStateNetwork>>

    @GET("question/{idWeb}/follow/{good}")
    fun updateFollow(@Path("idWeb") v: Int, @Path("good") good: Int, @Query("token") token: String): Observable<ApiSingleResponse<QuestionNetwork>>
}
