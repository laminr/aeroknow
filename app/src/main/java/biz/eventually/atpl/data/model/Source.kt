package biz.eventually.atpl.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by thibault on 20/03/17.
 */
data class Source(val id: Int, val name: String): Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Source> = object : Parcelable.Creator<Source> {
            override fun createFromParcel(source: Parcel): Source = Source(source)
            override fun newArray(size: Int): Array<Source?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readInt(),
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
    }
}
