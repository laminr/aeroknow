package biz.eventually.atpl.ui.source

import android.util.Log
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.model.Source
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.data.network.Question

import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by thibault on 20/03/17.
 */
@Singleton
class QuestionsManager @Inject constructor (private val dataProvider: DataProvider): RxBaseManager() {

    companion object {
        val TAG = "QuestionsManager"
    }

    fun getQuestions(topicId: Int, display: (Topic) -> Unit) {
        dataProvider.dataGetTopicQuestions(topicId)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ s ->
            display(s)
        }, { error ->
            Log.d(TAG, "getNbrQuestions: "+error)
        })

    }
}


