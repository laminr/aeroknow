package biz.eventually.atpl.ui.questions

import biz.eventually.atpl.data.db.Question

/**
 * Created by Thibault de Lambilly on 03/12/2017.
 *
 */
data class QuestionState(var question: Question, var index: Int, var size: Int) {

    var isLast: Boolean = false
        get() { return index == size }
}