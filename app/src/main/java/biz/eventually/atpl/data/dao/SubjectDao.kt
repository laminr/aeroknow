package biz.eventually.atpl.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.dto.SubjectView
import timber.log.Timber

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Dao
abstract class SubjectDao: BaseDao<Subject>() {

    @Query("SELECT * FROM subject")
    abstract fun getAll(): LiveData<List<Subject>>

//    @Query("SELECT idWeb, source_id, name FROM subject")
//    abstract fun getAllView(): LiveData<List<SubjectView>>

    @Query("SELECT idWeb FROM subject")
    abstract fun getIds(): List<Long>

    @Query("SELECT * FROM subject WHERE idWeb = :idWeb")
    abstract fun findById(idWeb: Long): Subject?
}