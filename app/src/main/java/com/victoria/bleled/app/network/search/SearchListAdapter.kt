package com.victoria.bleled.app.network.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.BR
import com.victoria.bleled.R
import com.victoria.bleled.data.model.Repo
import com.victoria.bleled.databinding.ItemRepoBinding
import com.victoria.bleled.util.architecture.base.BindingViewHolder

class SearchListAdapter(private val context: Context) :
    RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {

    private var products: List<Repo> = arrayListOf()
    var listener: AdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(position, products[position])
    }

    fun setList(contacts: List<Repo>) {
        this.products = contacts
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BindingViewHolder<ItemRepoBinding>(itemView) {
        fun bind(pos: Int, contact: Repo) {
            if (binding == null) {
                return
            }

            binding.tvTitle.text = contact.name
            binding.setVariable(
                BR.tintlist,
                context.resources.getColorStateList(R.color.xml_color_btn_title)
            )

            binding.root.setOnClickListener(View.OnClickListener {
                if (listener != null) {
                    listener?.onClick(pos, contact)
                }
            })
        }
    }

    interface AdapterListener {
        fun onClick(position: Int, info: Repo)
    }
}