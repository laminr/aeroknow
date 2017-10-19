package biz.eventually.atpl.ui.source

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 19/10/17.
 */
@Singleton
class ViewModelFactory(private val sRepo: SourceRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SourceViewModel::class.java)) {
            return SourceViewModel(sRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}