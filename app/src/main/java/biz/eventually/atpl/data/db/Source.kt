package biz.eventually.atpl.data.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by thibault on 20/03/17.
 */
open class Source() : RealmObject() {

    @PrimaryKey
    @Required
    var id : String = UUID.randomUUID().toString()

    var idWeb: Int = -1

    var name: String = ""

    constructor(idWeb: Int, name: String) : this() {
        this.idWeb = idWeb
        this.name = name
    }
}
