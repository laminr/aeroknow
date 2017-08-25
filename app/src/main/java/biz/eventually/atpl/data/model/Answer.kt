package biz.eventually.atpl.data.model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by thibault on 21/03/17.
 */
open class Answer() : RealmObject(), Comparable<Answer>, Parcelable {

    @PrimaryKey
    @Required
    var id: String = UUID.randomUUID().toString()

    var idWeb: Int = -1

    var value: String = ""

    var good: Boolean = false

    override fun compareTo(other: Answer) = kotlin.comparisons.compareValuesBy(this, other, { it.value })

    constructor(idWeb: Int, value: String, good: Boolean) : this() {
        this.idWeb = idWeb
        this.value = value
        this.good = good

    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        idWeb = parcel.readInt()
        value = parcel.readString()
        good = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(idWeb)
        parcel.writeString(value)
        parcel.writeByte(if (good) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Answer> {
        override fun createFromParcel(parcel: Parcel): Answer {
            return Answer(parcel)
        }

        override fun newArray(size: Int): Array<Answer?> {
            return arrayOfNulls(size)
        }
    }
}