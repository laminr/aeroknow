package biz.eventually.atpl.ui.source

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import biz.eventually.atpl.R
import biz.eventually.atpl.data.model.Source
import butterknife.BindView
import butterknife.ButterKnife

import kotlinx.android.synthetic.main.item_source.*

/**
 * Created by thibault on 20/03/17.
 */
class SourceAdapter(
        context: Context,
        sources: List<Source>) : ArrayAdapter<Source>(context, 0, sources) {

    var name: TextView? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val source = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_source, parent, false)
        }

        name = view?.findViewById(R.id.item_source_label) as TextView
        name?.text = source?.name

        return view
    }

}
