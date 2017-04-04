package biz.eventually.atpl.utils

import android.text.Html
import android.text.Spanned

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