package com.victoria.bleled.app.essential.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.databinding.ItemGallerySelectBinding
import com.victoria.bleled.util.feature.gallary.Gallary
import java.util.*

class GallerySelectAdapter constructor(var selectMax: Int = 1) :
    RecyclerView.Adapter<GallerySelectAdapter.ViewHolder>() {
    var list: ArrayList<Gallary> = arrayListOf()
    var listener: ((Int) -> Unit)? = null

    fun getSelectedImageList(): ArrayList<Gallary> {
        val arrImage = ArrayList<Gallary>()
        for (i in list.indices) {
            if (list[i].isIs_select === true) {
                arrImage.add(list[i])
            }
        }

        arrImage.sortWith(Comparator { lhs, rhs -> lhs.getNum() - rhs.getNum() })
        return arrImage
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(this, position, item)
    }

    class ViewHolder private constructor(val binding: ItemGallerySelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapter: GallerySelectAdapter, pos: Int, item: Gallary) {
            binding.item = item

            if (adapter.selectMax == 1) {
                binding.tvCnt.visibility = View.GONE
                binding.ivCheck.visibility = View.VISIBLE
            } else {
                binding.tvCnt.visibility = View.VISIBLE
                binding.ivCheck.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                if (adapter.selectMax == 1) {
                    for (idx in 0 until adapter.itemCount) {
                        adapter.list[idx].isIs_select = idx == pos
                    }
                    adapter.notifyDataSetChanged()

                    if (adapter.listener != null) {
                        adapter.listener!!(pos)
                    }
                    return@setOnClickListener
                } else {
                    if (adapter.listener != null) {
                        adapter.listener!!(pos)
                    }
                }
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGallerySelectBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}