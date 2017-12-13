package biz.eventually.atpl.ui.questions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.db.Question
import biz.eventually.atpl.data.dto.SubjectView
import biz.eventually.atpl.data.dto.TopicView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 26/11/2017.
 *
 */
@Singleton
class QuestionViewModel @Inject constructor(val repository: QuestionRepository) : ViewModel() {

    // topicId: Long, isSync: Boolean, hasOffline: Boolean
    var updateLine : MutableLiveData<Triple<Long, Boolean, Boolean>> = MutableLiveData()

    var networkStatus: LiveData<NetworkStatus> = repository.networkStatus()

    private var mPosition: MutableLiveData<Int> = MutableLiveData()

    var question: LiveData<QuestionState> = Transformations.switchMap(mPosition) {
        mPosition.value?.let {

            mAnswerIndexTick = -1

            val data = MutableLiveData<QuestionState>()
            mQuestionState = mQuestionState.copy(
                    question = mQuestions[it],
                    index = mPosition.value ?: 0
            )
            data.postValue(mQuestionState)

            return@switchMap data
        }
    }

    private var mQuestions: List<Question> = listOf()
    private var mQuestionState = QuestionState(Question(-1, -1, "", ""), 0, 0)
    private var mAnswerIndexTick = -1

    fun launchTest(topicId: Long, starFist: Boolean) {
        repository.getQuestions(topicId, starFist, fun(data: List<Question>) {
            if (data.isNotEmpty()) {
                mQuestions = data
                mQuestionState.size = data.size
                mPosition.value = 0
            }
        })
    }

    fun getDataForSubject(subjectId : Long, subjects: List<SubjectView>) {

        subjects.forEach {
            // here the topic is in fact a Subject, w/ idWeb = idWeb * -1
            if (it.subject.idWeb == (subjectId * -1)) {
                it.topics.forEach { topic ->
                    val id = topic.idWeb
                    // show sync on the line
                    updateLine.value = Triple(id, true, true)
                    repository.getWebData(id, false, true) {
                        // sync done for that line
                        updateLine.value = Triple(id, false, true)
                    }
                }
                // show the subject button download
                updateLine.postValue(Triple(subjectId, false, true))
            }
        }
    }
    private fun updateFollow(good: Boolean) {
        val index = mPosition.value ?: -1

        question.value?.let {
            repository.updateFollow(it.question.idWeb, good) { question ->
                question?.let {
                    if (index > -1) {
                        // updating the question data in case of return on it
                        when (good) {
                            true -> mQuestions[index].good = question.good
                            false -> mQuestions[index].wrong = question.wrong
                        }
                    }
                }
            }
        }
    }

    fun updateFocus(good: Boolean, then: (state: Boolean?) -> Unit) {
        question.value?.let {
            repository.updateFocus(it.question.idWeb, good) { care -> then(care) }
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
        var isGood: Boolean? = null

        if (mAnswerIndexTick > -1) {
            isGood = mQuestionState.question.answers[mAnswerIndexTick].good

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
                isGood = mQuestionState.question.answers[mAnswerIndexTick].good

                if (follow) {
                    updateFollow(isGood as Boolean)
                }
            }

            if (it < mQuestions.size - 1) mPosition.postValue(it + 1)
        }

        return isGood
    }

    fun shuffle() {
        mQuestions = mQuestions.shuffled()
        mPosition.value = 0
    }

}