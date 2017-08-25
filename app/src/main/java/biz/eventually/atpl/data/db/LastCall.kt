package biz.eventually.atpl.data.db

import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Thibault de Lambilly on 26/08/2017.
 */
open class LastCall() : RealmObject() {

    companion object {
        val COL_NAME = "name"

        val TYPE_SOURCE = "source"
        val TYPE_SUBJECT = "subject"
        val TYPE_TOPIC = "topic"
        val TYPE_QUESTION = "question"
    }

    @PrimaryKey
    @Required
    var id : String = ""

    var updated: Long = Date().time

    constructor(type: String) : this() {
        this.id = type
    }

    fun update(type: String, value: Long) {

        val lastCall: LastCall? = LastCall().queryFirst({ query -> query.equalTo("id", type) })

        lastCall?.let {
            it.updated = value
        } ?: kotlin.run {
            LastCall(type).apply {
                updated = value
                save()
            }
        }

    }
}