package biz.eventually.atpl.utils

import android.text.Html
import android.text.Spanned
import java.util.*

/**
 * Created by Thibault de Lambilly on 04/04/17.
 */

fun getHtml(txt: String): Spanned {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        return Html.fromHtml(txt, Html.FROM_HTML_MODE_LEGACY)
    } else {
        return Html.fromHtml(txt)
    }
}

fun <T:Comparable<T>>shuffle(items:MutableList<T>):List<T>{
    val rg : Random = Random()
    for (i in 0..items.size - 1) {
        val randomPosition = rg.nextInt(items.size)
        val tmp : T = items[i]
        items[i] = items[randomPosition]
        items[randomPosition] = tmp
    }
    return items
}