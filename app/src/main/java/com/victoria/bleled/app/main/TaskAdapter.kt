package com.victoria.bleled.app.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.databinding.ItemTaskBinding

class TaskAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var list: ArrayList<String> = arrayListOf()
    var listener: ((Int) -> Unit)? = null

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this, position)
    }

    class ViewHolder private constructor(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapter: TaskAdapter, pos: Int) {
            binding.viewmodel = adapter.viewModel
            binding.root.setOnClickListener {
                adapter.viewModel.openTask(binding.root, adapter.list[pos])
            }
            binding.data = adapter.list[pos]
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                /*parent가 없으면 부모의 match_parent를 얻어올수가 없음**/
                val binding = ItemTaskBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}