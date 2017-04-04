package biz.eventually.atpl.ui.subject

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import biz.eventually.atpl.R
import biz.eventually.atpl.network.model.dto.TopicDto

/**
 * Created by Thibault de Lambilly on 29/03/2017.
 */
class SubjectViewHolder(itemView: View, val itemClick: (TopicDto) -> Unit) : RecyclerView.ViewHolder(itemView) {

    private var mTitle: TextView = itemView.findViewById(R.id.subject_title) as TextView
    private var mCard: CardView = itemView.findViewById(R.id.topic_card) as CardView
    private var mTopicName: TextView = itemView.findViewById(R.id.topic_item_name) as TextView
    private var mQuestionNumber: TextView = itemView.findViewById(R.id.topic_item_questions) as TextView

    fun bind(topic: TopicDto) {

        with(topic) {
            mTitle.text = "processing...."
            if (id == -1) {
                mTitle.text = name

                mTitle.visibility = View.VISIBLE
                mCard.visibility = View.GONE
            } else {
                mTopicName.text = name
                mQuestionNumber.text = questions.toString()

                mTitle.visibility = View.GONE
                mCard.visibility = View.VISIBLE
            }
            itemView.setOnClickListener { itemClick(this) }
        }
    }
}