package biz.eventually.atpl.data.dao

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import biz.eventually.atpl.data.db.Source
import timber.log.Timber


/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Dao
abstract class SourceDao: BaseDao<Source>() {

    @Query("SELECT * FROM source")
    abstract fun getAll(): LiveData<List<Source>>

    @Query("SELECT idWeb FROM source")
    abstract fun getIds(): List<Long>

    @Query("SELECT * FROM source WHERE idWeb = :idWeb")
    abstract fun findById(idWeb: Long): Source?

}