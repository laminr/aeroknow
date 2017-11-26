package biz.eventually.atpl.ui.questions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.db.Question
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 26/11/2017.
 */
@Singleton
class QuestionViewModel @Inject constructor(val dao: QuestionRepository) : ViewModel() {

    var networkStatus: LiveData<NetworkStatus> = dao.networkStatus()

    private var mCurrent: MutableLiveData<Int> = MutableLiveData()

    private var mQuestions : List<Question> = mutableListOf()

    var question : LiveData<Question> = Transformations.switchMap(mCurrent) {
        mCurrent.value?.let {
            val data = MutableLiveData<Question>()
            data.postValue(mQuestions[it])
            return@switchMap data
        }
    }
}