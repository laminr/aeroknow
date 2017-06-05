package biz.eventually.atpl.data.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Thibault de Lambilly on 04/06/2017.
 */
open class Focus() : RealmObject() {

    @PrimaryKey
    @Required
    var id : String = UUID.randomUUID().toString()

    var idWeb : Int = -1
    var questionId: Int = -1
    var care: Boolean = false

    constructor(idWeb: Int, questionId: Int, care: Boolean) : this() {
        this.idWeb = idWeb
        this.questionId = questionId
        this.care = care
    }
}