package biz.eventually.atpl.utils

import android.content.Context
import android.net.ConnectivityManager
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.data.db.Focus
import biz.eventually.atpl.data.model.Question
import com.vicpin.krealmextensions.queryAll
import java.util.*


/**
 * Created by Thibault de Lambilly on 04/04/17.
 */

fun <T : Comparable<T>> shuffle(items: MutableList<T>): List<T> {
    val rg: Random = Random()
    val times = if (items.size < 5) 4 else 2

    for (j in 0..times) {
        for (i in 0..items.size - 1) {
            val randomPosition = rg.nextInt(items.size)
            val tmp: T = items[i]
            items[i] = items[randomPosition]
            items[randomPosition] = tmp
        }
    }
    return items
}

fun List<Question>.orderByFollowAndFocus(): MutableList<Question> {
    var care: List<Question> = arrayListOf()
    var dontCare: List<Question> = arrayListOf()
    var others: List<Question> = arrayListOf()

    val focusPrimary = Focus().queryAll()
    val focus = mutableMapOf<Int, Focus>()

    focusPrimary.forEach { f ->
        focus[f.questionId] = f
    }

    this.forEach { question ->
        val hasFocus = focus[question.idWeb]

        if (hasFocus != null) {
            when (hasFocus.care) {
                true -> care += question
                false -> dontCare += question
            }
        } else {
            others += question
        }
    }

    val data: MutableList<Question> = mutableListOf()
    data.addAll(care)
    data.addAll(others)
    data.addAll(dontCare)

    return data
}

fun hasInternetConnection(): Boolean {
    val cm = AtplApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}