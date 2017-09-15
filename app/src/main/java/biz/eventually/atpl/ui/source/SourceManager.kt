package biz.eventually.atpl.ui.source

import android.util.Log
import biz.eventually.atpl.R
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.db.LastCall
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.utils.hasInternetConnection
import com.google.firebase.perf.metrics.AddTrace
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class SourceManager @Inject constructor(private val dataProvider: DataProvider) : RxBaseManager() {

    companion object {
        val TAG = "SourceManager"
    }

    @AddTrace(name = "getSources", enabled = true)
    fun getSources(fromDb: Boolean, display: (List<Source>?) -> Unit, error: (Int) -> Unit) {

        if (fromDb) {
            val sourcesDb: MutableList<Source> = Source().queryAll().toMutableList()
            if (sourcesDb.size > 0) {
                display(sourcesDb)
            } else {
                error(R.string.error_no_network)
            }
        } else {
            getData()?.let(display) ?: kotlin.run({ error(R.string.error_network_error) })
        }
    }

    private fun getData(): List<Source>? {

        val sourcesDb: MutableList<Source> = Source().queryAll().toMutableList()

        if (hasInternetConnection()) {
            dataProvider
                    .dataGetSources()
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ sWeb -> analyseData(sourcesDb, sWeb) }, { e -> Log.d(TAG, "getSources: " + e) })
        }

        return if (sourcesDb.size > 0) sourcesDb else null
    }

    private fun analyseData(sourcesDb: MutableList<Source>, sWeb: List<Source>) {

        val sourceIds = sourcesDb.map { it.idWeb }

        sWeb.forEach { s ->
            // Update
            if (s.idWeb in sourceIds) {
                // updating value
                val source = Source().queryFirst { query -> query.equalTo("idWeb", s.idWeb) }
                source?.let {
                    it.name = s.name
                    it.save()
                }
            }
            // New
            else {
                s.save()
                sourcesDb.add(s)
            }
        }

        // update time reference
        if (sWeb.isNotEmpty()) LastCall().update(LastCall.TYPE_SOURCE, Date().time)
    }
}


