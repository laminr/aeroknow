package biz.eventually.atpl.di.module

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Debug
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.dao.SourceDao
import biz.eventually.atpl.data.dao.SubjectDao
import biz.eventually.atpl.data.dao.TopicDao
import biz.eventually.atpl.ui.source.SourceRepository
import biz.eventually.atpl.ui.subject.SubjectRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context) : AppDatabase {
        val builder = Room.databaseBuilder(context, AppDatabase::class.java,  "aeroknow.db").fallbackToDestructiveMigration()

        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    /**
     * Dao
     */
    @Singleton
    @Provides
    fun provideSourceDao(db: AppDatabase) : SourceDao {
        return db.sourceDao()
    }

    @Singleton
    @Provides
    fun provideSubjectDao(db: AppDatabase) : SubjectDao {
        return db.subjectDao()
    }

    @Singleton
    @Provides
    fun provideTopicDao(db: AppDatabase) : TopicDao {
        return db.topicDao()
    }

    /**
     * Repositories
     */
    @Singleton
    @Provides
    fun provideSourceRepository(dataProvider: DataProvider, dao: SourceDao): SourceRepository {
        return SourceRepository(dataProvider, dao)
    }

    @Singleton
    @Provides
    fun provideSubjectRepository(dataProvider: DataProvider, dao: SubjectDao,tDao: TopicDao): SubjectRepository {
        return SubjectRepository(dataProvider, dao, tDao)
    }
}