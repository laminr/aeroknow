package biz.eventually.atpl.data.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Thibault de Lambilly on 04/06/2017.
 */
open class Follow() : RealmObject() {

    @PrimaryKey
    @Required
    var id : String = UUID.randomUUID().toString()

    var idWeb : Int = 0
    var questionId: Int = 0
    var good: Int = 0
    var wrong: Int = 0

    var total: Int = good + wrong

    constructor(idWeb : Int, questionId: Int, good: Int, wrong: Int) : this() {
        this.idWeb = idWeb
        this.questionId = questionId
        this.good = good
        this.wrong = wrong
    }

    fun increment(good: Boolean) = when (good) {
        true -> this.good += 1
        false -> this.wrong += 1
    }

}