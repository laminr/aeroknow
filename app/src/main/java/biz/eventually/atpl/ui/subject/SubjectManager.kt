package biz.eventually.atpl.ui.subject

import android.util.Log
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import com.vicpin.krealmextensions.*
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
    fun getSubjects(sourceId: Long, display: (List<Subject>?) -> Unit, error: () -> Unit) {

        if (hasInternetConnection()) {
            dataProvider.dataGetSubjects(sourceId).subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ subWeb ->

                analyseData(subWeb, sourceId)

                val subjectsDb = Subject().query({ s -> s.equalTo("sourceId", sourceId) })
                display(subjectsDb)

            }, { e ->
                Log.d(TAG, TAG + ".getSubjects: " + e)
                error()
            })
        } else {
            val subjectsDb = Subject().query({ s -> s.equalTo("sourceId", sourceId) })
            display(subjectsDb)
        }
    }

    private fun analyseData(subWeb: List<Subject>, sourceId: Long) {

        val subjectsIds = Subject().query({ s -> s.equalTo("sourceId", sourceId) }).map { it.idWeb }

        subWeb.forEach { subjectWeb ->
            // Update
            if (subjectWeb.idWeb in subjectsIds) {
                Subject().queryFirst({ query -> query.equalTo("idWeb", subjectWeb.idWeb) })
                        ?.let {
                            // update name
                            it.name = subjectWeb.name

                            // update topics
                            val topicDbIds = it.topics.map { it.idWeb }
                            val topicWebIds = subjectWeb.topics.map { it.idWeb }

                            // topics to update
                            it.topics.filter({ t -> t.idWeb in topicWebIds }).forEach { t ->
                                val topicWeb = subjectWeb.topics.first { tw -> tw.idWeb == t.idWeb }
                                t.name = topicWeb.name
                                t.questions = topicWeb.questions
                                t.focus = topicWeb.focus
                                t.follow = topicWeb.follow
                                t.save()
                            }

                            // new one to save
                            for (topic in subjectWeb.topics.filter({ t -> t.idWeb !in topicDbIds })) {
                                // must be saved before add to topics
                                topic.save()
                                it.topics.add(topic)
                            }

                            // update the subject
                            it.save()
                        }
            }
            // New
            else {
                subjectWeb.sourceId = sourceId
                subjectWeb.save()
            }
        }

        // update time reference
        if (subWeb.isNotEmpty()) LastCall().update(LastCall.TYPE_SUBJECT, Date().time)
    }
}