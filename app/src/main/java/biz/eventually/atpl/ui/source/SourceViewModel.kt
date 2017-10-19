package biz.eventually.atpl.ui.source

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import biz.eventually.atpl.data.db.Source
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 18/10/17.
 */
@Singleton
class SourceViewModel @Inject constructor(val repository: SourceRepository) : ViewModel() {

    var sources: LiveData<List<Source>> = MutableLiveData<List<Source>>()
        get() {
            return this.repository.getSources()
        }

}