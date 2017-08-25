package biz.eventually.atpl.ui.subject

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import biz.eventually.atpl.R
import biz.eventually.atpl.data.model.Topic


/**
 * Created by laminr on 29/03/2017.
 */
class SubjectAdapter(val onClick: (dto: Topic, startFirst: Boolean) -> Unit) : RecyclerView.Adapter<SubjectViewHolder>() {

    var name: TextView? = null
    private var mTopics = mutableListOf<Topic>()
    private var mParent: ViewGroup? = null

    override fun onBindViewHolder(holder: SubjectViewHolder?, position: Int) {
        holder?.apply {
            bind(mTopics[position])
        }
    }

    override fun getItemCount(): Int {
        return mTopics.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SubjectViewHolder {
        mParent = parent
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_subject_row, parent, false)
        return SubjectViewHolder(view, onClick)
    }

    fun bind(topics: List<Topic>): Unit {
        mTopics = mutableListOf()
        mTopics.addAll(topics)
    }
}