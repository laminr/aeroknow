package biz.eventually.atpl.utils

/**
 * Created by Thibault de Lambilly on 19/06/2017.
 */

import android.content.Context
import android.preference.PreferenceManager
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R

object Prefields {
    val PREF_TIMER_NBR: String = AtplApplication.get().getString(R.string.pref_timer_nbr)
    val PREF_TIMER_ENABLE : String = AtplApplication.get().getString(R.string.pref_timer_enable)
    val PREF_TOKEN : String = AtplApplication.get().getString(R.string.pref_token)
    val PREF_LAST_DATA : String = AtplApplication.get().getString(R.string.pref_last_data)
}

fun prefsGetString(context: Context, key: String, defValue: String? = null) : String? {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.getString(key, defValue)
}

fun prefsPutString(context: Context, key: String, value: String) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    editor.putString(key, value)
    editor.apply()
}

fun prefsPutInt(context: Context, key: String, value: Int) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    editor.putInt(key, value)
    editor.apply()
}

inline fun <reified T> prefsGetValue(key: String, defValue: T): T {
    val pref = PreferenceManager.getDefaultSharedPreferences(AtplApplication.get())
    return when(T::class) {
        Int::class -> pref.getInt(key, defValue as Int) as T
        String::class -> pref.getString(key, defValue as String) as T
        Boolean::class -> pref.getBoolean(key, defValue as Boolean) as T
        Long::class -> pref.getLong(key, defValue as Long) as T
        else -> defValue
    }
}

fun prefsPutBool(context: Context, key: String, defValue: Boolean): Boolean {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.getBoolean(key, defValue)
}

fun getInt(context: Context, key: String, defValue: Int): Int {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.getInt(key, defValue)
}

fun putInt(context: Context, key: String, value: Int) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    editor.putInt(key, value)
    editor.apply()
}

fun getLong(context: Context, key: String, defValue: Long): Long {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.getLong(key, defValue)
}

fun putLong(context: Context, key: String, value: Long) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    editor.putLong(key, value)
    editor.apply()
}

fun exists(context: Context, key: String): Boolean {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.contains(key)
}

fun remove(context: Context, key: String) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    editor.remove(key)
    editor.apply()
}