package biz.eventually.atpl.data.dao

import android.arch.persistence.room.*

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Dao
interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(type: T): Long

    @Insert
    fun insert(vararg obj: T)

    @Update
    fun update(type: T)

    @Delete
    fun delete(type: T)
}