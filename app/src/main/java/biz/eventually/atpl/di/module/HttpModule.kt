package biz.eventually.atpl.di.module

import android.content.SharedPreferences
import android.preference.PreferenceManager
import biz.eventually.atpl.AtplApplication

import com.squareup.moshi.Moshi

import javax.inject.Singleton

import biz.eventually.atpl.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by laminr on 18/03/2017.
 */
@Module
class HttpModule {

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    internal fun providesSharedPreferences(application: AtplApplication): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    internal fun provideOkHttpCache(application: AtplApplication): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cache = Cache(application.cacheDir, cacheSize.toLong())
        return cache
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(interceptor)
                .build()
    }

    @Provides
    @Singleton
    internal fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.API_ATPL_ENDPOINT)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

}
