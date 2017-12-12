package biz.eventually.atpl.ui.subject

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import biz.eventually.atpl.data.NetworkStatus
import biz.eventually.atpl.data.db.Topic
import biz.eventually.atpl.data.dto.SubjectView
import biz.eventually.atpl.data.dto.TopicView
import biz.eventually.atpl.ui.questions.QuestionRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 18/10/17.
 *
 */
@Singleton
class SubjectViewModel @Inject constructor(val repository: SubjectRepository) : ViewModel() {

    private var sourceId: MutableLiveData<Long> = MutableLiveData()

    var networkStatus: LiveData<NetworkStatus> = repository.networkStatus()

    var subjects: LiveData<List<SubjectView>> = Transformations.switchMap(sourceId) {
        repository.getSubjects(it)
    }

    fun setSourceId(sourceId: Long) {
        this.sourceId.value = sourceId
    }

    fun getTopicIdWithQuestion() = repository.getTopicIdWithQuestion()
}