package biz.eventually.atpl.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import biz.eventually.atpl.data.dao.LastCallDao
import java.util.*
import javax.inject.Inject

/**
 * Created by Thibault de Lambilly on 26/08/2017.
 *
 */
@Entity(tableName = "last_call")
class LastCall(@PrimaryKey val type: String) {

    companion object {
        val TYPE_SOURCE = "source"
        val TYPE_TOPIC = "topic"
    }

    var updatedAt: Long = Date().time

    @Ignore
    constructor(type: String, updatedAt: Long): this(type) {
        this.updatedAt = updatedAt
    }
}