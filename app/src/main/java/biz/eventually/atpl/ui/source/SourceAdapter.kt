package biz.eventually.atpl.ui.source

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import biz.eventually.atpl.R
import biz.eventually.atpl.data.db.Source

import kotlinx.android.synthetic.main.item_source.*

/**
 * Created by Thibault de Lambilly on 20/03/17.
 */
class SourceAdapter(
        context: Context,
        sources: List<Source>) : ArrayAdapter<Source>(context, 0, sources) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val vh: ListRowHolder
        val source = getItem(position)

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_source, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        vh.label?.text = source?.name

        return view
    }

    private class ListRowHolder(row: View?) {
        val label: TextView? = row?.findViewById(R.id.item_source_label)
    }
}
