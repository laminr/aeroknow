package biz.eventually.atpl.data.model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by thibault on 20/03/17.
 */

open class Question() : RealmObject(), Comparable<Question>, Parcelable {

    @PrimaryKey
    @Required
    var id: String = UUID.randomUUID().toString()

    var topicId: Int = -1

    var idWeb: Int = -1

    var label: String = ""

    var answers: RealmList<Answer> = RealmList()

    var img: String = ""

    val imgList : List<String>
        get() = explodeImgRaw(img)

    var focus: Boolean? = null

    var follow: Follow? = null

    override fun compareTo(other: Question) = compareValuesBy(this, other, { it.label })

    constructor(idWeb: Int, label: String, answers: RealmList<Answer>, img: String?, focus: Boolean?, follow: Follow?) : this() {

        this.idWeb = idWeb
        this.label = label
        this.answers = answers
        this.img = img ?: ""
        this.focus = focus
        this.follow = follow

    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        topicId = parcel.readInt()
        idWeb = parcel.readInt()
        label = parcel.readString()
        img = parcel.readString()
        focus = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        follow = parcel.readParcelable(Follow::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(topicId)
        parcel.writeInt(idWeb)
        parcel.writeString(label)
        parcel.writeString(img)
        parcel.writeValue(focus)
        parcel.writeParcelable(follow, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }

    fun explodeImgRaw(raw: String?): List<String> = raw?.split("|")?.toList() ?: listOf()
}