package biz.eventually.atpl.ui.subject

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import biz.eventually.atpl.R
import biz.eventually.atpl.network.model.Subject
import biz.eventually.atpl.network.model.Topic


/**
 * Created by laminr on 29/03/2017.
 */
class SubjectAdapter : RecyclerView.Adapter<SubjectViewHolder>() {

    var name: TextView? = null
    private var mTopics = mutableListOf<Topic>()

    override fun onBindViewHolder(holder: SubjectViewHolder?, position: Int) {
        holder?.apply {
            bind(mTopics[position])
        }
    }

    override fun getItemCount(): Int {
        return mTopics.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_subject_row, parent, false)
        return SubjectViewHolder(view)
    }

    fun bind(topics: List<Topic>) : Unit {
        mTopics.addAll(topics)
    }
}