package com.victoria.bleled.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.base.internal.Inflate

abstract class BaseListAdapter<T, IVB : ViewDataBinding>(
    private val inflate: Inflate<IVB>,
    private val diff: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, BaseListAdapter<T, IVB>.BaseViewHolder>(diff) {
    abstract fun onBind(item: T, binding: IVB, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = inflate.invoke(LayoutInflater.from(parent.context), parent, false)
        return object : BaseViewHolder(binding) {
            override fun bind(item: T, position: Int) {
                onBind(item, this.binding, position)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    abstract inner class BaseViewHolder(
        protected val binding: IVB,
    ) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: T, position: Int)
    }

    companion object {
        fun <T : Any> diffSimple(f: (o: T, n: T) -> Boolean): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return f(oldItem, newItem)
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}

