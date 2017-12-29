package biz.eventually.atpl.di.module

import biz.eventually.atpl.data.service.SourceService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by Thibault de Lambilly on 21/03/17.
 */
@Module
class ServiceModule {

    @Provides
    @Singleton
    internal fun provideSourceService(retrofit: Retrofit): SourceService {
        return retrofit.create(SourceService::class.java)
    }

}
