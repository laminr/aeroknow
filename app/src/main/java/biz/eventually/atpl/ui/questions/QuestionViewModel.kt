package biz.eventually.atpl.ui.questions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.db.Question
import kotlinx.android.synthetic.main.activity_questions.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 26/11/2017.
 *
 */
@Singleton
class QuestionViewModel @Inject constructor(val repository: QuestionRepository) : ViewModel() {

    var networkStatus: LiveData<NetworkStatus> = repository.networkStatus()

    private var mPosition: MutableLiveData<Int> = MutableLiveData()

    var question: LiveData<Question> = Transformations.switchMap(mPosition) {
        mPosition.value?.let {

            mAnswerIndexTick = -1

            val data = MutableLiveData<Question>()
            mCurrentQuestion = mQuestions[it]
            data.postValue(mCurrentQuestion)

            return@switchMap data
        }
    }

    private var mQuestions: List<Question> = listOf()
    private var mCurrentQuestion = Question(-1, -1, "", "")
    private var mAnswerIndexTick = -1

    fun launchTest(topicId: Long, starFist: Boolean) {
        repository.getQuestions(topicId, starFist, fun(data: List<Question>) {
            if (data.isNotEmpty()) {
                mQuestions = data
                mPosition.value = 0
            }
        })
    }

    fun updateFollow(good: Boolean) {
        val currentIndex = mPosition

        question.value?.let {
            repository.updateFollow(it.idWeb, good, fun(question: Question?) {
                question?.let {
                    currentIndex.value?.let {
                        when (good) {
                            true -> mQuestions[it].good += 1
                            false -> mQuestions[it].wrong += 1
                        }
                    }
                }
            })
        }
    }

    fun updateFocus(good: Boolean, then: (state: Boolean?) -> Unit) {
        question.value?.let {
            repository.updateFocus(it.idWeb, good, then)
        }
    }

    fun tickAnswer(tick: Int) {
        mAnswerIndexTick = tick
    }

    /**
     * change index position for previous question
     * returning if ever the answer to the question was good.
     */
    fun previous(follow: Boolean): Boolean? {
        var isGood : Boolean? =  null

        if (mAnswerIndexTick > -1) {
            isGood = mCurrentQuestion.answers[mAnswerIndexTick].good

            // server following
            if (follow) {
                updateFollow(isGood)
            }
        }

        mPosition.value?.let {
            if (it >= 1) mPosition.postValue(it - 1)
        }

        return isGood
    }

    /**
     * change index position for next question
     * returning if ever the answer to the question was good.
     */
    fun next(follow: Boolean): Boolean? {

        var isGood: Boolean? = null

        mPosition.value?.let {
            if (mAnswerIndexTick > -1 && it < mQuestions.size) {
                isGood = mCurrentQuestion.answers[mAnswerIndexTick].good

                if (follow) {
                    updateFollow(isGood as Boolean)
                }
            }

            if (it < mQuestions.size - 1) mPosition.postValue(it + 1)
        }

        return isGood
    }

    fun getTestSize() : Int  = mQuestions.size
    fun getCurrentIndex() : Int  = mPosition.value ?: -1
    fun isLastQuestion() : Boolean = getCurrentIndex() == getTestSize() - 1
}