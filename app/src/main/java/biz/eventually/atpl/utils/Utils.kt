package biz.eventually.atpl.utils

import android.content.Context
import android.net.ConnectivityManager
import biz.eventually.atpl.AtplApplication
import java.util.*


/**
 * Created by Thibault de Lambilly on 04/04/17.
 *
 */

fun <T : Comparable<T>> shuffle(items: MutableList<T>): List<T> {
    val rg: Random = Random()
    val times = if (items.size < 5) 4 else 2

    for (j in 0..times) {
        for (i in 0 until items.size) {
            val randomPosition = rg.nextInt(items.size)
            val tmp: T = items[i]
            items[i] = items[randomPosition]
            items[randomPosition] = tmp
        }
    }
    return items
}

fun hasInternetConnection(): Boolean {
    val cm = AtplApplication.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

/**
 * Functions that check several object for null state, then return their non null version
 */
fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

fun <T1, T2, T3> ifNotNull(value1: T1?, value2: T2?, value3: T3?, then: (T1, T2, T3) -> (Unit)) {
    if (value1 != null && value2 != null && value3 != null) {
        then(value1, value2, value3)
    }
}