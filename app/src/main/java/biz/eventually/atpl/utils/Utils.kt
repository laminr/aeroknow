package biz.eventually.atpl.utils

import android.content.Context
import android.net.ConnectivityManager
import biz.eventually.atpl.AtplApplication
import java.util.*


/**
 * Created by Thibault de Lambilly on 04/04/17.
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