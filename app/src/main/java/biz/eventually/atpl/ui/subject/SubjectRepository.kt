package biz.eventually.atpl.ui.subject

import android.arch.lifecycle.LiveData
import biz.eventually.atpl.R
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.dao.LastCallDao
import biz.eventually.atpl.data.dao.SubjectDao
import biz.eventually.atpl.data.dao.TopicDao
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.db.Topic
import biz.eventually.atpl.data.dto.SubjectView
import biz.eventually.atpl.ui.BaseRepository
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 20/03/17.
 *
 */
@Singleton
class SubjectRepository @Inject constructor(private val dataProvider: DataProvider, private val dao: SubjectDao, private val topicDao: TopicDao, private val lastCallDao: LastCallDao) : BaseRepository() {

    @AddTrace(name = "getSubjects", enabled = true)
    fun getSubjects(sourceId: Long): LiveData<List<SubjectView>> {
        return dao.findBySourceId(sourceId)
    }

    fun getWebData(sourceId: Long, isSilent: Boolean = false) {
        if (hasInternetConnection()){
            doAsync {
                // always from scratch...
                val lastCall =  0L
                uiThread {
                    if (!isSilent) status.postValue(NetworkStatus.LOADING)
                    disposables += dataProvider
                            .dataGetSubjects(sourceId, lastCall)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ subWeb ->
                                analyseData(subWeb, sourceId)
                                if (!isSilent) status.postValue(NetworkStatus.SUCCESS)
                            }, { e ->
                                Timber.d("getSources: " + e)
                                if (!isSilent) status.postValue(NetworkStatus.ERROR)
                                error(R.string.error_network_error)
                            })
                }
            }
        }
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
            lastCallDao.updateOrInsert(LastCall("${LastCall.TYPE_SOURCE}_$sourceId", Date().time))
        }
    }

    fun getTopicIdWithQuestion(): List<Long> = topicDao.getTopicIdWithQuestion()
}