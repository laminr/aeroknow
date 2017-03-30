package biz.eventually.atpl.network.service

import biz.eventually.atpl.network.network.SourceNetwork
import biz.eventually.atpl.network.network.SubjectNetwork
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
}
