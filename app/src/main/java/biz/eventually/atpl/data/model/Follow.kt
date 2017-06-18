package biz.eventually.atpl.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Thibault de Lambilly on 18/06/2017.
 */
data class Follow(var good: Int = 0, var wrong: Int = 0) : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Follow> = object : Parcelable.Creator<Follow> {
            override fun createFromParcel(source: Parcel): Follow = Follow(source)
            override fun newArray(size: Int): Array<Follow?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(good)
        dest.writeInt(wrong)
    }
}
