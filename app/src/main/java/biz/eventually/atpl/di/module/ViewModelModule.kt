package biz.eventually.atpl.di.module

import biz.eventually.atpl.ui.ViewModelFactory
import biz.eventually.atpl.ui.source.SourceRepository
import biz.eventually.atpl.ui.source.SourceViewModel
import biz.eventually.atpl.ui.subject.SubjectRepository
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
    fun provideSourceViewModelFactory(repository: SourceRepository): ViewModelFactory<SourceRepository> {
        return ViewModelFactory(repository)
    }

    @Singleton
    @Provides
    fun provideSubjectViewModelFactory(repository: SubjectRepository): ViewModelFactory<SubjectRepository> {
        return ViewModelFactory(repository)
    }

    @Singleton
    @Provides
    fun provideSourceViewModel(repository: SourceRepository): SourceViewModel {
        return SourceViewModel(repository)
    }
}