package biz.eventually.atpl.ui.subject

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import biz.eventually.atpl.R
import biz.eventually.atpl.data.dto.TopicView
import biz.eventually.atpl.data.db.Topic


/**
 * Created by Thibault de Lambilly on 29/03/2017.
 */
class SubjectAdapter(val onClick: (dto: Topic, startFirst: Boolean) -> Unit) : RecyclerView.Adapter<SubjectViewHolder>() {

    var name: TextView? = null
    private var mTopics = mutableListOf<TopicView>()
    private lateinit var mParent: ViewGroup

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.apply {
            bind(mTopics[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        mParent = parent
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject_row, parent, false)
        return SubjectViewHolder(view, onClick)

    }

    override fun getItemCount(): Int {
        return mTopics.count()
    }

    fun bind(topics: List<TopicView>) {
        mTopics = mutableListOf()
        mTopics.addAll(topics)
    }

    fun getList() : MutableList<TopicView> {
        return mTopics
    }
}