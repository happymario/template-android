package com.victoria.bleled.app.essential.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.databinding.ItemGalleryTopBinding
import com.victoria.bleled.util.feature.gallary.Gallary

class GalleryTopAdapter : RecyclerView.Adapter<GalleryTopAdapter.ViewHolder>() {
    var list: ArrayList<Gallary> = arrayListOf()
    var listener: ((Int, Int) -> Unit)? = null

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(this, position, item)
    }


    class ViewHolder private constructor(val binding: ItemGalleryTopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapter: GalleryTopAdapter, pos: Int, item: Gallary) {
            binding.data = item
            binding.imageButton6.setOnClickListener {
                if (adapter.listener != null) {
                    adapter.listener!!(1, pos)
                }
            }
            binding.ivImage.setOnClickListener(View.OnClickListener {
                if (adapter.listener != null) {
                    adapter.listener!!(0, pos)
                }
            })
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGalleryTopBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}