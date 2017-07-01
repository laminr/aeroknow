package biz.eventually.atpl.data.network

import android.os.Parcel
import android.os.Parcelable
import biz.eventually.atpl.data.model.Follow

/**
 * Created by thibault on 20/03/17.
 */
data class Question(
        val id: Int,
        val label: String,
        var answers: List<Answer>,
        val img: List<String>?,
        var focus: Boolean?,
        var follow: Follow
) : Comparable<Question>, Parcelable {
    override fun compareTo(other: Question) = compareValuesBy(this, other, { it.label })

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Question> = object : Parcelable.Creator<Question> {
            override fun createFromParcel(source: Parcel): Question = Question(source)
            override fun newArray(size: Int): Array<Question?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readInt(),
    source.readString(),
    source.createTypedArrayList(Answer.CREATOR),
    source.createStringArrayList(),
    source.readValue(Boolean::class.java.classLoader) as Boolean?,
    source.readParcelable<Follow>(Follow::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(label)
        dest.writeTypedList(answers)
        dest.writeStringList(img)
        dest.writeValue(focus)
        dest.writeParcelable(follow, 0)
    }
}