package biz.eventually.atpl.data.network

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by thibault on 20/03/17.
 */
data class Question(val id: Int, val label: String, val answers: List<Answer>, val img: List<String>?) : Comparable<Question>, Parcelable {
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
            ArrayList<Answer>().apply { source.readList(this, Answer::class.java.classLoader) },
            source.createStringArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(label)
        dest?.writeList(answers)
        dest?.writeStringList(img)
    }
}