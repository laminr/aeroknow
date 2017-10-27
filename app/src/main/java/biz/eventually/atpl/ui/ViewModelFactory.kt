package biz.eventually.atpl.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import biz.eventually.atpl.ui.source.SourceRepository
import biz.eventually.atpl.ui.source.SourceViewModel
import biz.eventually.atpl.ui.subject.SubjectRepository
import biz.eventually.atpl.ui.subject.SubjectViewModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 19/10/17.
 */
@Singleton
class ViewModelFactory<REPO> @Inject constructor(private val sRepo: REPO) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
            return SubjectViewModel(sRepo as SubjectRepository) as T
        }

        if (modelClass.isAssignableFrom(SourceViewModel::class.java)) {
            return SourceViewModel(sRepo as SourceRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}