package biz.eventually.atpl.network.model

import android.os.Parcel
import android.os.Parcelable
import biz.eventually.atpl.network.network.Question

/**
 * Created by thibault on 20/03/17.
 */
data class Topic(val id: Int, val name: String, val questions: List<Question>, val follow: Int, val focus: Int) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Topic> = object : Parcelable.Creator<Topic> {
            override fun createFromParcel(source: Parcel): Topic = Topic(source)
            override fun newArray(size: Int): Array<Topic?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readInt(), source.readString(), ArrayList<Question>().apply{ source.readList(this, Question::class.java.classLoader) }, source.readInt(), source.readInt())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(name)
        dest?.writeList(questions)
        dest?.writeInt(follow)
        dest?.writeInt(focus)
    }
}