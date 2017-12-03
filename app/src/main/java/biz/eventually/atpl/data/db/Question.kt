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
        indices = [Index(value = ["topic_id"], name = "idx_question_topic_id")],
        foreignKeys = [ForeignKey(
                entity = Topic::class,
                parentColumns = ["idWeb"],
                childColumns = ["topic_id"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE

        )]
)
class Question(@PrimaryKey
               var idWeb: Long,
               @ColumnInfo(name = "topic_id")
               var topicId: Long,
               var label: String,
               var img: String = "",
               var focus: Boolean? = null,
               var good: Int = 0,
               var wrong: Int = 0
) : Comparable<Question>, Parcelable {

    @Ignore
    val imgList: List<String> = explodeImgRaw(img)

    @Ignore
    var answers: List<Answer> = listOf()

    override fun compareTo(other: Question) = compareValuesBy(this, other, { it.label })

    private fun explodeImgRaw(raw: String?): List<String> = raw?.split("|")?.toList() ?: listOf()

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readInt(),
            parcel.readInt()) {
        answers = parcel.createTypedArrayList(Answer)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(idWeb)
        parcel.writeLong(topicId)
        parcel.writeString(label)
        parcel.writeString(img)
        parcel.writeValue(focus)
        parcel.writeInt(good)
        parcel.writeInt(wrong)
        parcel.writeTypedList(answers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question = Question(parcel)

        override fun newArray(size: Int): Array<Question?> = arrayOfNulls(size)
    }


}