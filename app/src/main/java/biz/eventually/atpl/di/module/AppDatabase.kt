package biz.eventually.atpl.di.module

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import biz.eventually.atpl.data.AtplTypeConverter
import biz.eventually.atpl.data.dao.SourceDao
import biz.eventually.atpl.data.db.Source

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Database(entities = arrayOf( Source::class ), version = 1)
@TypeConverters(AtplTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sourceDao() : SourceDao

}