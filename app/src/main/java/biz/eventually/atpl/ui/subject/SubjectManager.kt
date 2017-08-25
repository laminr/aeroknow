package biz.eventually.atpl.ui.subject

import android.util.Log
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.ui.source.SourceManager
import biz.eventually.atpl.utils.hasInternetConnection
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import com.google.firebase.perf.metrics.AddTrace
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 28/03/17.
 */

@Singleton
class SubjectManager @Inject constructor(private val dataProvider: DataProvider) : RxBaseManager() {

    companion object {
        val TAG = "SubjectManager"
    }

    @AddTrace(name = "getSubjects", enabled = true)
    fun getSubjects(sourceId: Int, display: (List<Subject>?) -> Unit, error: () -> Unit) {

        val subjectsDb = Subject().query({ s -> s.equalTo("sourceId", sourceId) }).toMutableList()

        if (hasInternetConnection()) {
            dataProvider.dataGetSubjects(sourceId).subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ subWeb ->

                analyseData(subjectsDb, subWeb, sourceId)
                display(subjectsDb)

            }, { e ->
                Log.d(TAG, TAG + ".getSubjects: " + e)
                error()
            })
        } else {
            display(subjectsDb)
        }
    }

    private fun analyseData(subjectsDb: MutableList<Subject>, subWeb: List<Subject>, sourceId: Int) {

        val subjectsIds = subjectsDb.map { it.idWeb }

        subWeb.forEach { subjectWeb ->
            // Update
            if (subjectWeb.idWeb in subjectsIds) {
                Subject().queryFirst({ query -> query.equalTo("idWeb", subjectWeb.idWeb) })
                        ?.let {
                            // update name
                            it.name = subjectWeb.name

                            // update topics
                            val topicIds = it.topics.map { it.idWeb }

                            subjectWeb.topics.forEach { topicWeb ->
                                if (topicWeb.idWeb in topicIds) {
                                    val topicDb = Topic().queryFirst { query -> query.equalTo("idWeb", topicWeb.idWeb) }
                                    topicDb?.let {
                                        it.name = topicWeb.name
                                        it.questions = topicWeb.questions
                                        it.focus = topicWeb.focus
                                        it.follow = topicWeb.follow

                                        it.save()
                                    }
                                } else {
                                    topicWeb.save()
                                    it.topics.add(topicWeb)
                                }
                            }

                            it.save()
                        }
            }
            // New
            else {
                subjectWeb.sourceId = sourceId
                subjectWeb.save()

                subjectsDb.add(subjectWeb)
            }
        }

        // update time reference
        if (subWeb.isNotEmpty()) LastCall().update(LastCall.TYPE_SUBJECT, Date().time)
    }
}