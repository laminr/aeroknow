package biz.eventually.atpl.data.db

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Thibault de Lambilly on 21/03/17.
 *
 */
@Entity(
        tableName = "answer",
        indices = arrayOf(Index(value = "question_id", name = "idx_answer_question_id")),
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Question::class,
                        parentColumns = arrayOf("idWeb"),
                        childColumns = arrayOf("question_id"),
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE

                )
        )
)
class Answer(@PrimaryKey
             @ColumnInfo(name = "idWeb")
             var idWeb: Long,
             @ColumnInfo(name = "question_id")
             var questionId: Long,
             var value: String = "",
             var good: Boolean = false) : Comparable<Answer>, Parcelable {

    override fun compareTo(other: Answer) = kotlin.comparisons.compareValuesBy(this, other, { it.value })

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(idWeb)
        parcel.writeLong(questionId)
        parcel.writeString(value)
        parcel.writeByte(if (good) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Answer> {
        override fun createFromParcel(parcel: Parcel): Answer = Answer(parcel)

        override fun newArray(size: Int): Array<Answer?> = arrayOfNulls(size)
    }
}