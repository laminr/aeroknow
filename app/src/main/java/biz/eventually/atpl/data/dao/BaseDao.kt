package biz.eventually.atpl.data.dao

import android.arch.persistence.room.*

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Dao
abstract interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(type: T): Long

    @Insert
    abstract fun insert(vararg obj: T)

    @Update
    abstract fun update(type: T)

    @Delete
    abstract  fun delete(type: T)
}