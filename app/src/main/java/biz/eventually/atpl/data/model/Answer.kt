package biz.eventually.atpl.data.network

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by thibault on 21/03/17.
 */
data class Answer(val id: Int, val value: String, val good: Boolean) : Comparable<Answer>, Parcelable {
    override fun compareTo(other: Answer) = kotlin.comparisons.compareValuesBy(this, other, { it.value })

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(value)
        writeInt((if (good) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Answer> = object : Parcelable.Creator<Answer> {
            override fun createFromParcel(source: Parcel): Answer = Answer(source)
            override fun newArray(size: Int): Array<Answer?> = arrayOfNulls(size)
        }
    }
}