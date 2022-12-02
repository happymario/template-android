package com.victoria.bleled.app.recent.hilt

import com.victoria.bleled.base.BaseListAdapter
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.databinding.GithubRepoItemBinding

class GithubRepoAdapter() :
    BaseListAdapter<ModelUser, GithubRepoItemBinding>(
        GithubRepoItemBinding::inflate,
        diffSimple { o, n -> o.id == n.id }
    ) {

    var onClickItem: ((item: ModelUser) -> Unit)? = null

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.bind(getItem(position), position)
    }

    override fun onBind(
        item: ModelUser,
        binding: GithubRepoItemBinding,
        position: Int,
    ) {
        binding.data = item
        binding.root.setOnClickListener {
            onClickItem?.let { it1 -> it1(item) }
        }
        binding.executePendingBindings()
    }
}