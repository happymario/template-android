package com.victoria.bleled.app.recent.hilt

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.victoria.bleled.R
import com.victoria.bleled.base.BaseBindingVMFragment
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.databinding.GithubDetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GithubDetailFragment() :
    BaseBindingVMFragment<GithubDetailFragmentBinding, GithubDetailViewModel>() {

    private val args by navArgs<GithubDetailFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: GithubDetailViewModel.Factory
    override val viewModel: GithubDetailViewModel by viewModels {
        GithubDetailViewModel.provideFactory(viewModelFactory, args.data.id!!)
    }
    override val layoutId: Int = R.layout.github_detail_fragment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            vm = viewModel
        }
    }

    override fun observeViewEvent() {
        super.observeViewEvent()

        binding.backAction.setOnClickListener {
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "key",
                "value that needs to be passed"
            )
            //navController.popBackStack()
            navController.navigateUp()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()

        viewModel.detail.observe(this) {
            setData(it)
        }

        viewModel.setData(args.data)
    }

    fun setData(data: ModelUser) {
        binding.tvDetail.text = data.id;
    }
}