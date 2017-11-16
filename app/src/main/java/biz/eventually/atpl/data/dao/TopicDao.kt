package biz.eventually.atpl.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import biz.eventually.atpl.data.db.Topic

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Dao
abstract class TopicDao : BaseDao<Topic> {

    @Query("SELECT * FROM topic")
    abstract fun getAll(): LiveData<List<Topic>>

    @Query("SELECT idWeb FROM topic")
    abstract fun getIds(): List<Long>

    @Query("SELECT * FROM topic WHERE idWeb = :idWeb")
    abstract fun findById(idWeb: Long): Topic?

    @Query("SELECT * FROM topic WHERE subject_id = :idWeb")
    abstract fun findBySubjectId(idWeb: Long): List<Topic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(type: List<Topic>)
}