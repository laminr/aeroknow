package biz.eventually.atpl.ui.subject

import android.support.v7.widget.RecyclerView
import android.view.View
import biz.eventually.atpl.data.model.dto.TopicDto
import kotlinx.android.synthetic.main.item_subject_row.view.*

/**
 * Created by Thibault de Lambilly on 29/03/2017.
 */
class SubjectViewHolder(itemView: View, val itemClick: (TopicDto) -> Unit) : RecyclerView.ViewHolder(itemView) {

    fun bind(topic: TopicDto) {

        with(topic) {
            itemView.subject_title.text = "processing...."
            if (id == -1) {
                itemView.subject_title.text = name

                itemView.subject_title.visibility = View.VISIBLE
                itemView.topic_card.visibility = View.GONE

                itemView.topic_done_ico.visibility = View.GONE
                itemView.topic_done_nbr.visibility = View.GONE
                itemView.topic_care_ico.visibility = View.GONE
                itemView.topic_care_nbr.visibility = View.GONE
            } else {
                itemView.topic_item_name.text = name
                itemView.topic_item_questions.text = questions.toString()

                itemView.subject_title.visibility = View.GONE
                itemView.topic_card.visibility = View.VISIBLE

                // follow
                if (follow > 0) {
                    itemView.topic_done_nbr.text = follow.toString()
                }
                itemView.topic_done_ico.visibility = if (follow == 0) View.GONE else View.VISIBLE
                itemView.topic_done_nbr.visibility = if (follow == 0) View.GONE else View.VISIBLE

                // focus
                if (focus > 0) {
                    itemView.topic_care_nbr.text = focus.toString()
                }
                itemView.topic_care_ico.visibility = if (focus == 0) View.GONE else View.VISIBLE
                itemView.topic_care_nbr.visibility = if (focus == 0) View.GONE else View.VISIBLE
            }

            itemView.setOnClickListener { itemClick(this) }
        }
    }
}