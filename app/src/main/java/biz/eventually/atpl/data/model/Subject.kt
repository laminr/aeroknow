package biz.eventually.atpl.data.model

import android.os.Parcel
import android.os.Parcelable
import biz.eventually.atpl.data.model.dto.TopicDto

/**
 * Created by thibault on 20/03/17.
 */
data class Subject(val id: Int, val name: String, val topics: List<TopicDto>) : Parcelable{

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Subject> = object : Parcelable.Creator<Subject> {
            override fun createFromParcel(source: Parcel): Subject = Subject(source)
            override fun newArray(size: Int): Array<Subject?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readInt(),
    source.readString(),
    ArrayList<TopicDto>().apply { source.readList(this, TopicDto::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeList(topics)
    }
}

