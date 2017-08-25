package biz.eventually.atpl.data.model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Thibault de Lambilly on 18/06/2017.
 */
open class Follow() : RealmObject(), Parcelable {

    @PrimaryKey
    @Required
    var id : String = UUID.randomUUID().toString()

    var good: Int = 0
    var wrong: Int = 0

    constructor(good: Int = 0, wrong: Int = 0): this() {
        this.good = good
        this.wrong = wrong
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        good = parcel.readInt()
        wrong = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(good)
        parcel.writeInt(wrong)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Follow> {
        override fun createFromParcel(parcel: Parcel): Follow {
            return Follow(parcel)
        }

        override fun newArray(size: Int): Array<Follow?> {
            return arrayOfNulls(size)
        }
    }
}
