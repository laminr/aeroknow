package biz.eventually.atpl.ui.source

import android.util.Log
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.model.Source

import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers

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

    fun getSources(display: (List<Source>?) -> Unit, error: () -> Unit) {

        dataProvider.dataGetSources()?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ s ->
            display(s)
        }, { error ->
            Log.d(TAG, "getSources: "+error)
            error()
        })

    }
}


