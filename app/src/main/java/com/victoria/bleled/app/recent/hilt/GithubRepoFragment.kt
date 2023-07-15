package com.victoria.bleled.app.recent.hilt

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R
import com.victoria.bleled.base.BaseBindingVMFragment
import com.victoria.bleled.databinding.GithubRepoFragmentBinding
import com.victoria.bleled.util.CommonUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GithubRepoFragment() :
    BaseBindingVMFragment<GithubRepoFragmentBinding, GithubRepoViewModel>() {
    override val viewModel: GithubRepoViewModel by viewModels()
    override val layoutId: Int = R.layout.github_repo_fragment

    private lateinit var itemAdapter: GithubRepoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            vm = viewModel
        }
    }

    override fun observeViewEvent() {
        super.observeViewEvent()

        binding.backAction.setOnClickListener {
            requireActivity().finish()
        }

        itemAdapter = GithubRepoAdapter()
        binding.rvItem.adapter = itemAdapter
        itemAdapter.onClickItem = {
            findNavController().navigate(
                GithubRepoFragmentDirections.actionGithubRepoFragmentToGithubDetailFragment()
            )
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")
            ?.observe(viewLifecycleOwner) {
                if (it != "") {
                    CommonUtil.showToast(requireContext(), it)
                }
            }

        // recycle load More
        binding.rvItem.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (itemAdapter.itemCount != 0) {
                    val lastPos = linearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                    if (lastPos != RecyclerView.NO_POSITION && lastPos == (itemAdapter.itemCount - 1)) {
                        val page = viewModel.curPage.plus(1)
                        viewModel.getList(page, "")
                    }
                }
            }
        })
    }

    override fun observeViewModel() {
        super.observeViewModel()

        viewModel.itemList.observeResource(binding.loading, binding.rvItem) {
            itemAdapter.submitList(it)
        }

        viewModel.getList(1, "")
    }
}