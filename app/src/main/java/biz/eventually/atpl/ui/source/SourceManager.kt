package biz.eventually.atpl.ui.source

import android.util.Log
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.model.Source
import com.google.firebase.perf.metrics.AddTrace
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class SourceManager @Inject constructor (private val dataProvider: DataProvider): RxBaseManager() {

    companion object {
        val TAG = "SourceManager"
    }

    @AddTrace(name = "getSources", enabled = true)
    fun getSources(display: (List<Source>?) -> Unit, error: () -> Unit) {

        dataProvider.dataGetSources()
                .subscribeOn(scheduler.network)
                ?.observeOn(scheduler.main)
                ?.subscribe({ s ->
            display(s)
        }, { e ->
            Log.d(TAG, "getSources: "+e)
            error()
        })

    }
}


