package com.victoria.bleled.app.essential.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.victoria.bleled.R
import com.victoria.bleled.util.feature.gallary.Folder

class GalleryFolderSpinnerAdapter constructor(context: Context) : BaseAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    val dataSource: ArrayList<Folder> = ArrayList()

    override fun getItem(position: Int): Any? {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_folder_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.label.text = dataSource.get(position).folderName

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_folder_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        val folder = dataSource.get(position)
        var folderName = folder.folderName
        if (folderName == null || folderName.isEmpty() || folderName.equals("null")) {
            folderName = vh.label.context.resources.getString(R.string.no_name)
        }
        vh.label.text = folderName + "(" + folder.images.size + ")"
        vh.label.setTextColor(vh.label.context.resources.getColor(R.color.common_text))

        return view
    }

    private class ItemHolder(row: View?) {
        val label: TextView

        init {
            label = row?.findViewById(R.id.tv_title) as TextView
        }
    }
}