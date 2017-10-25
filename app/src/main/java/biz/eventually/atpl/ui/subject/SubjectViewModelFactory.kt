package biz.eventually.atpl.ui.subject

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import biz.eventually.atpl.ui.source.SourceRepository
import biz.eventually.atpl.ui.source.SourceViewModel
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 19/10/17.
 */
@Singleton
class SubjectViewModelFactory(private val sRepo: SubjectRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
            return SubjectViewModel(sRepo) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}