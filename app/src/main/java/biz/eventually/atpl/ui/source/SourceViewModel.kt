package biz.eventually.atpl.ui.source

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import biz.eventually.atpl.data.db.Source
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 18/10/17.
 */
@Singleton
class SourceViewModel @Inject constructor(val repository: SourceRepository) : ViewModel() {

    private lateinit var sources : LiveData<List<Source>>

    init {
        sources = repository.getSources()
    }

    var data : LiveData<List<Source>> = sources

    fun refreshData() {
        sources = repository.getSources()
    }
}