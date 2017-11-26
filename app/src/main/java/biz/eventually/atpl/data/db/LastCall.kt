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
class LastCall(val type: String) {

    @Ignore
    @Inject
    lateinit var dao : LastCallDao

    companion object {
        val COL_NAME = "name"

        val TYPE_SOURCE = "source"
        val TYPE_SUBJECT = "subject"
        val TYPE_TOPIC = "topic"
        val TYPE_QUESTION = "question"
    }

    @PrimaryKey
    var id : String = ""

    var updatedAt: Long = Date().time

    @Ignore
    constructor(type: String, updatedAt: Long): this(type) {
        this.updatedAt = updatedAt
    }

    fun findByType(type: String) : LastCall? = dao.findByType(type)

    fun update() {
        dao.findByType(type)?.let {
            it.updatedAt = updatedAt
            dao.update(it)
        } ?: dao.insert(LastCall(type).apply { updatedAt = this.updatedAt })

    }
}