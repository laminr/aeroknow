package biz.eventually.atpl.data

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Created by Thibault de Lambilly on 17/10/17.
 */

object AtplTypeConverter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? = if (null == value) null else Date(value)

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? = date?.time
}