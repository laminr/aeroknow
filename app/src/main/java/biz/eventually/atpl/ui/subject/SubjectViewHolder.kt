package biz.eventually.atpl.ui.subject

import android.icu.util.ValueIterator
import android.support.v7.widget.RecyclerView
import android.view.View
import biz.eventually.atpl.data.model.dto.TopicDto
import kotlinx.android.synthetic.main.item_subject_row.view.*

/**
 * Created by Thibault de Lambilly on 29/03/2017.
 */
class SubjectViewHolder(itemView: View, val itemClick: (TopicDto, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView) {

    fun bind(topic: TopicDto) {

        itemView.subject_title.visibility = View.VISIBLE
        itemView.topic_card.visibility = View.VISIBLE

        itemView.topic_done_ico.visibility = View.VISIBLE
        itemView.topic_done_nbr.visibility = View.VISIBLE

        itemView.topic_care_ico.visibility = View.VISIBLE
        itemView.topic_care_nbr.visibility = View.VISIBLE

        itemView.topic_item_questions_ico.visibility = View.VISIBLE
        itemView.topic_item_questions.visibility = View.VISIBLE


        with(topic) {
            itemView.subject_title.text = "processing...."
            // Title
            if (id == -1) {
                itemView.subject_title.text = name

                itemView.subject_title.visibility = View.VISIBLE
                itemView.topic_card.visibility = View.GONE

                itemView.topic_done_ico.visibility = View.GONE
                itemView.topic_done_nbr.visibility = View.GONE
                itemView.topic_care_ico.visibility = View.GONE
                itemView.topic_care_nbr.visibility = View.GONE
            }
            // Value Card
            else {
                // Hide title
                itemView.subject_title.visibility = View.GONE
                // Show card
                itemView.topic_card.visibility = View.VISIBLE

                itemView.topic_item_name.text = name
                itemView.topic_item_questions.text = questions.toString()
                itemView.topic_done_nbr.text = follow.toString()

                // follow
                if (follow == 0) {

                    itemView.topic_done_ico.visibility = View.GONE
                    itemView.topic_done_nbr.visibility = View.GONE
                    
                } else if (follow == questions) {

                    itemView.topic_item_questions_ico.visibility = View.GONE
                    itemView.topic_item_questions.visibility = View.GONE

                }
//                else {
//
//                    itemView.topic_done_ico.visibility = View.VISIBLE
//                    itemView.topic_done_nbr.visibility = View.VISIBLE
//                }

                // care
                if (focus > 0) {
                    itemView.topic_care_nbr.text = focus.toString()
                }
                itemView.topic_care_ico.visibility = if (focus == 0) View.GONE else View.VISIBLE
                itemView.topic_care_nbr.visibility = if (focus == 0) View.GONE else View.VISIBLE
            }

            itemView.setOnClickListener { itemClick(this, false) }
            itemView.topic_care_ico.setOnClickListener { itemClick(this, true) }
            itemView.topic_care_nbr.setOnClickListener { itemClick(this, true) }
        }
    }
}