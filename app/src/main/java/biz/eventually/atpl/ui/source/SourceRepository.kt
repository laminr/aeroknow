package biz.eventually.atpl.ui.source

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import biz.eventually.atpl.R
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.dao.SourceDao
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class SourceRepository @Inject constructor(private val dataProvider: DataProvider, private val dao: SourceDao) : RxBaseManager() {

    private var status: MutableLiveData<NetworkStatus> = MutableLiveData()

    @AddTrace(name = "getSources", enabled = true)
    fun getSources(): LiveData<List<Source>> {
        return dao.getAll()
    }

    fun updateData() {
        if (hasInternetConnection()) getWebData()
    }

    fun networkStatus(): LiveData<NetworkStatus> {
        return status
    }

    private fun getWebData() {

        status.postValue(NetworkStatus.LOADING)
        disposables += dataProvider
                .dataGetSources()
                .subscribeOn(scheduler.network)
                .observeOn(scheduler.main)
                .subscribe({ sWeb ->
                    analyseData(sWeb)
                    status.postValue(NetworkStatus.SUCCESS)
                }, { e ->
                    Timber.d("getSources: " + e)
                    status.postValue(NetworkStatus.ERROR)
                    error(R.string.error_network_error)
                })

    }

    private fun analyseData(sWeb: List<Source>) {

        doAsync {
            val sourceIds = dao.getIds()

            sWeb.forEach { s ->
                // Update
                if (s.idWeb in sourceIds) {
                    s.idWeb?.let {
                        Maybe.just(it).observeOn(scheduler.disk).map {
                            val sourceDb = dao.findById(it)
                            sourceDb?.let {
                                it.name = s.name
                                dao.update(it)
                            }
                        }
                    }
                }
                // New
                else {
                    dao.insert(s)
                }
            }

            // update time reference
            if (sWeb.isNotEmpty()) LastCall(LastCall.TYPE_SOURCE, Date().time).update()
        }
    }
}


