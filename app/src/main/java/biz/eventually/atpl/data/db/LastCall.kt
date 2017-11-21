package biz.eventually.atpl.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import biz.eventually.atpl.data.dao.LastCallDao
import java.util.*
import javax.inject.Inject

/**
 * Created by Thibault de Lambilly on 26/08/2017.
 *
 */
@Entity(tableName = "lastCall")
class LastCall(val type: String) {

    companion object {
        val COL_NAME = "name"

        val TYPE_SOURCE = "source"
        val TYPE_SUBJECT = "subject"
        val TYPE_TOPIC = "topic"
        val TYPE_QUESTION = "question"

        @Inject
        private  lateinit var dao : LastCallDao

        fun findByType(type: String) : LastCall? = dao.findByType(type)

        fun update(type: String, value: Long) {
            dao.findByType(type)?.let {
                it.updated = value
                dao.update(it)
            } ?: dao.insert(LastCall(type).apply { updated = value })

        }
    }

    @PrimaryKey
    var id : String = ""

    var updated: Long = Date().time
}