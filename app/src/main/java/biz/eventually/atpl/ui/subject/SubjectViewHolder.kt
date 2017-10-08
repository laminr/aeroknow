package biz.eventually.atpl.ui.subject

import android.support.v7.widget.RecyclerView
import android.view.View
import biz.eventually.atpl.R
import biz.eventually.atpl.data.dto.TopicView
import biz.eventually.atpl.data.model.Topic
import kotlinx.android.synthetic.main.item_subject_row.view.*

/**
 * Created by Thibault de Lambilly on 29/03/2017.
 */
class SubjectViewHolder(itemView: View, private val itemClick: (Topic, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView) {

    fun bind(dto: TopicView) {

        itemView.subject_title.visibility = View.VISIBLE

        itemView.topic_mean_nbr.visibility = View.VISIBLE
        itemView.topic_card.visibility = View.VISIBLE

        itemView.topic_done_ico.visibility = View.VISIBLE
        itemView.topic_done_nbr.visibility = View.VISIBLE

        itemView.topic_care_ico.visibility = View.VISIBLE
        itemView.topic_care_nbr.visibility = View.VISIBLE

        itemView.topic_item_questions_ico.visibility = View.VISIBLE
        itemView.topic_item_questions.visibility = View.VISIBLE

        // is sync, change icon
        if (dto.isSync) {
            itemView.topic_offline.setImageResource(R.drawable.ic_sync)
            itemView.topic_offline.visibility = View.VISIBLE
        } else {
            itemView.topic_offline.setImageResource(R.drawable.ic_cloud_off_black)
            itemView.topic_offline.visibility = View.GONE
        }

        with(dto.topic) {
            itemView.subject_title.text = itemView.context.getString(R.string.msg_processing)
            // Title
            if (idWeb < 0) {
                itemView.subject_title.text = name

                itemView.subject_title.visibility = View.VISIBLE
                itemView.topic_download.visibility = View.VISIBLE

                itemView.topic_card.visibility = View.GONE

                itemView.topic_done_ico.visibility = View.GONE
                itemView.topic_done_nbr.visibility = View.GONE
                itemView.topic_care_ico.visibility = View.GONE
                itemView.topic_care_nbr.visibility = View.GONE

                itemView.topic_download.setOnClickListener { itemClick(this, true) }
            }
            // Value Card
            else {
                // Hide title
                itemView.subject_title.visibility = View.GONE
                itemView.topic_download.visibility = View.GONE

                // Show card
                itemView.topic_card.visibility = View.VISIBLE

                // offline
                if (!dto.isSync) {
                    itemView.topic_offline.visibility = if (dto.hasOfflineData) View.GONE else View.VISIBLE
                }

                itemView.topic_item_name.text = name

                itemView.topic_item_questions.text = questions.toString()
                itemView.topic_done_nbr.text = follow.toString()

                if (mean > 0) {
                    itemView.topic_mean_nbr.visibility = View.VISIBLE
                    itemView.topic_mean_nbr.text = itemView.context.getString(R.string.subject_mean_value, mean)
                } else {
                    itemView.topic_mean_nbr.visibility = View.GONE
                }

                // follow
                if (follow == 0) {

                    itemView.topic_done_ico.visibility = View.GONE
                    itemView.topic_done_nbr.visibility = View.GONE

                } else if (follow == questions) {

                    itemView.topic_item_questions_ico.visibility = View.GONE
                    itemView.topic_item_questions.visibility = View.GONE

                }

                // care
                if (focus > 0) {
                    itemView.topic_care_nbr.text = focus.toString()
                }
                itemView.topic_care_ico.visibility = if (focus == 0) View.GONE else View.VISIBLE
                itemView.topic_care_nbr.visibility = if (focus == 0) View.GONE else View.VISIBLE

                // only for dto lines
                itemView.setOnClickListener { itemClick(this, false) }
                itemView.topic_care_ico.setOnClickListener { itemClick(this, true) }
                itemView.topic_care_nbr.setOnClickListener { itemClick(this, true) }
            }
        }
    }
}