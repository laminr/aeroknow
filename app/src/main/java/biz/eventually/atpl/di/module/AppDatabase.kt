package biz.eventually.atpl.di.module

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import biz.eventually.atpl.data.AtplTypeConverter
import biz.eventually.atpl.data.dao.*
import biz.eventually.atpl.data.db.*

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Database(entities = [
    Source::class,
    Subject::class,
    Topic::class,
    Question::class,
    Answer::class,
    LastCall::class
], version = 2)
@TypeConverters(AtplTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sourceDao() : SourceDao
    abstract fun subjectDao() : SubjectDao
    abstract fun topicDao() : TopicDao
    abstract fun questionDao() : QuestionDao
    abstract fun lastCallDao() : LastCallDao
}