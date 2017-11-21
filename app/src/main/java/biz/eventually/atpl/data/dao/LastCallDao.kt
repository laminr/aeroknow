package biz.eventually.atpl.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import biz.eventually.atpl.data.db.LastCall

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */
@Dao
abstract class LastCallDao : BaseDao<LastCall> {

    @Query("SELECT * FROM lastCall WHERE idWeb = :id")
    abstract fun findByType(type: String): LastCall?
}