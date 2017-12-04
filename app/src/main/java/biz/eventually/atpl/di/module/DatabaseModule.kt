package biz.eventually.atpl.di.module

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Debug
import biz.eventually.atpl.data.DataProvider
import biz.eventually.atpl.data.dao.*
import biz.eventually.atpl.ui.questions.QuestionRepository
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
    fun provideSourceDao(db: AppDatabase) : SourceDao = db.sourceDao()

    @Singleton
    @Provides
    fun provideSubjectDao(db: AppDatabase) : SubjectDao = db.subjectDao()

    @Singleton
    @Provides
    fun provideTopicDao(db: AppDatabase) : TopicDao = db.topicDao()

    @Singleton
    @Provides
    fun provideQuestionDao(db: AppDatabase) : QuestionDao = db.questionDao()

    @Singleton
    @Provides
    fun provideLastCallDao(db: AppDatabase) : LastCallDao = db.lastCallDao()

    /**
     * Repositories
     */
    @Singleton
    @Provides
    fun provideSourceRepository(dataProvider: DataProvider, dao: SourceDao): SourceRepository =
            SourceRepository(dataProvider, dao)

    @Singleton
    @Provides
    fun provideSubjectRepository(dataProvider: DataProvider, dao: SubjectDao,tDao: TopicDao): SubjectRepository =
            SubjectRepository(dataProvider, dao, tDao)

    @Singleton
    @Provides
    fun provideQuestionRepository(dataProvider: DataProvider, dao: QuestionDao, lastDao: LastCallDao): QuestionRepository =
            QuestionRepository(dataProvider, dao, lastDao)
}