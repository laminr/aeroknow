package biz.eventually.atpl.data.network

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by thibault on 21/03/17.
 */
data class Answer(val id: Int, val value: String, val good: Boolean) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Answer> = object : Parcelable.Creator<Answer> {
            override fun createFromParcel(source: Parcel): Answer = Answer(source)
            override fun newArray(size: Int): Array<Answer?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            1.equals(source.readInt())
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(value)
        dest?.writeInt((if (good) 1 else 0))
    }
}