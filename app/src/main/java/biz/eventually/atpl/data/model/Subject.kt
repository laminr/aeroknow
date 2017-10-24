package biz.eventually.atpl.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by thibault on 20/03/17.
 */
open class Subject() : RealmObject() {

    @PrimaryKey
    @Required
    var id : String = UUID.randomUUID().toString()

    var sourceId: Long = -1

    var idWeb: Int = -1

    var name: String = ""

    var topics: RealmList<Topic> = RealmList()

    constructor(sourceId: Long, idWeb: Int, name: String, topics: RealmList<Topic>): this() {
        this.sourceId = sourceId
        this.idWeb = idWeb
        this.name = name
        this.topics = topics
    }
}

