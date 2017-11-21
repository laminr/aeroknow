package biz.eventually.atpl.data.db

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable
import biz.eventually.atpl.data.model.Follow

/**
 * Created by Thibault de Lambilly on 20/03/17.
 *
 */
@Entity(
        tableName = "question",
        indices = arrayOf(Index(value = "topic_id", name = "idx_question_topic_id")),
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Topic::class,
                        parentColumns = arrayOf("idWeb"),
                        childColumns = arrayOf("topic_id"),
                        onUpdate = ForeignKey.NO_ACTION,
                        onDelete = ForeignKey.CASCADE

                )
        )
)
class Question(@PrimaryKey
               var idWeb: Long,
               @ColumnInfo(name = "topic_id")
               var topicId: Int,
               var label: String,
               var img: String = "",
               var focus: Boolean? = null,
               var follow: Follow? = null
) : Comparable<Question>, Parcelable {

    @Ignore
    val imgList: List<String> = explodeImgRaw(img)

    @Ignore
    var answers: List<Answer> = listOf()

    override fun compareTo(other: Question) = compareValuesBy(this, other, { it.label })

    private fun explodeImgRaw(raw: String?): List<String> = raw?.split("|")?.toList() ?: listOf()

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readParcelable(Follow::class.java.classLoader)) {
        answers = parcel.createTypedArrayList(Answer)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(idWeb)
        parcel.writeInt(topicId)
        parcel.writeString(label)
        parcel.writeString(img)
        parcel.writeValue(focus)
        parcel.writeParcelable(follow, flags)
        parcel.writeTypedList(answers)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question = Question(parcel)

        override fun newArray(size: Int): Array<Question?> = arrayOfNulls(size)
    }
}