package biz.eventually.atpl.ui.subject

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.ui.source.SourceRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 18/10/17.
 */
@Singleton
class SubjectViewModel @Inject constructor(val repository: SubjectRepository) : ViewModel() {

    var isLoading : LiveData<Boolean> = repository.isLoading()

    fun getData(sourceId: Long) : LiveData<List<Subject>>{
        return repository.getSubjects(sourceId)
    }
}