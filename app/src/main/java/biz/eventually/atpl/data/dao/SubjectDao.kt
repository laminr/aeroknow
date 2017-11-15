package biz.eventually.atpl.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.dto.SubjectView

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Dao
abstract class SubjectDao : BaseDao<Subject> {

    @Query("SELECT * FROM subject")
    abstract fun getAll(): LiveData<List<Subject>>

    @Transaction // good practice with POJO w/ @Relation Object to ensure consistency
    @Query("SELECT * FROM subject WHERE source_id = :sourceId")
    abstract fun findBySourceId(sourceId: Long): LiveData<List<SubjectView>>


    @Query("SELECT idWeb FROM subject")
    abstract fun getIds(): List<Long>

    @Query("SELECT * FROM subject WHERE idWeb = :idWeb")
    abstract fun findById(idWeb: Long): Subject?
}