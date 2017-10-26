package biz.eventually.atpl.ui.subject

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import biz.eventually.atpl.R
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.dao.SubjectDao
import biz.eventually.atpl.data.dao.TopicDao
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.db.Topic
import biz.eventually.atpl.data.dto.SubjectView
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.toMaybe
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class SubjectRepository @Inject constructor(private val dataProvider: DataProvider, private val dao: SubjectDao, private val topicDao: TopicDao) : RxBaseManager() {

    private var loading: MutableLiveData<Boolean> = MutableLiveData()

    companion object {
        val TAG = "SourceRepository"
    }

    fun isLoading(): LiveData<Boolean> {
        return loading
    }

    @AddTrace(name = "getSubjects", enabled = true)
    fun getSubjects(sourceId: Long): LiveData<List<Subject>> {

        if (hasInternetConnection()) getWebData(sourceId)
        val data = dao.findBySourceId(sourceId)
        data.value?.forEach {
            it.topics = topicDao.findBySubjectId(it.idWeb)
        }

        return data
    }

    private fun getWebData(sourceId: Long) {

        disposables += dataProvider
                .dataGetSubjects(sourceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ subWeb ->
                    analyseData(subWeb, sourceId)
                }, { e ->
                    Timber.d("getSources: " + e)
                    loading.value = false
                    error(R.string.error_network_error)
                })
    }

    private fun analyseData(subWeb: List<Subject>, sourceId: Long) {

        doAsync {

            val subjectsIds = dao.getIds()

            subWeb.forEach { subjectWeb ->
                // Update
                if (subjectWeb.idWeb in subjectsIds) {
                    dao.findById(subjectWeb.idWeb)?.let {
                        // update name
                        it.name = subjectWeb.name

                        // update topicDbs
                        val topicDbs = topicDao.findBySubjectId(it.idWeb)
                        val topicDbIds = topicDbs.map { it.idWeb }
                        val topicWebIds = subjectWeb.topics?.map { it.idWeb } ?: listOf()

                        // topicDbs to update
                        topicDbs.filter({ t -> t.idWeb in topicWebIds }).forEach { t ->
                            subjectWeb.topics?.first { tw -> tw.idWeb == t.idWeb }?.let {
                                t.name = it.name
                                t.questions = it.questions
                                t.focus = it.focus
                                t.follow = it.follow

                                topicDao.update(t)
                            }
                        }

                        // Topics from web to save (not in topicDbIds)
                        val topicToInsert = mutableListOf<Topic>()
                        // must be saved before add to topicDbs
                        (subjectWeb.topics?.filter({ t -> t.idWeb !in topicDbIds }) ?: listOf()).mapTo(topicToInsert) { topic ->
                            Topic(topic.idWeb, it.idWeb, topic.name, topic.questions, topic.follow, topic.focus)
                        }

                        // saving topic if any
                        if (topicToInsert.isNotEmpty()) topicDao.insertAll(topicToInsert)

                        // update the subject
                        dao.update(it)
                    }
                }
                // New
                else {
                    val subjectId = dao.insert(Subject(subjectWeb.idWeb, sourceId, subjectWeb.name))
                    subjectWeb.topics?.forEach {
                        topicDao.insert(Topic(it.idWeb, subjectId, it.name, it.questions, it.follow, it.focus))
                    }
                }
            }

            // update time reference
            if (subWeb.isNotEmpty()) LastCall().update(LastCall.TYPE_SUBJECT, Date().time)
        }
    }
}