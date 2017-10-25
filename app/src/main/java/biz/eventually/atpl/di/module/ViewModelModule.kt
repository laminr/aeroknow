package biz.eventually.atpl.di.module

import biz.eventually.atpl.ui.source.SourceRepository
import biz.eventually.atpl.ui.source.SourceViewModel
import biz.eventually.atpl.ui.source.SourceViewModelFactory
import biz.eventually.atpl.ui.subject.SubjectRepository
import biz.eventually.atpl.ui.subject.SubjectViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 19/10/17.
 */
@Module
class ViewModelModule {

    @Singleton
    @Provides
    fun provideSourceViewModelFactory(repository: SourceRepository): SourceViewModelFactory {
        return SourceViewModelFactory(repository)
    }

    @Singleton
    @Provides
    fun provideSubjectViewModelFactory(repository: SubjectRepository): SubjectViewModelFactory {
        return SubjectViewModelFactory(repository)
    }

    @Singleton
    @Provides
//    @Named("sourceViewModel")
    fun provideSourceViewModel(repository: SourceRepository): SourceViewModel {
        return SourceViewModel(repository)
    }
}