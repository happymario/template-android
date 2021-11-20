package com.victoria.bleled.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.victoria.bleled.R
import com.victoria.bleled.databinding.FragmentMainBinding
import com.victoria.bleled.util.arch.base.BaseBindingFragment
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory
import timber.log.Timber
import java.util.*


class MainFragment : BaseBindingFragment<FragmentMainBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }
    private lateinit var listAdapter: TasksAdapter


    /************************************************************
     *  Overrides
     ************************************************************/
    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  super.onCreateView(inflater, container, savedInstanceState)
        binding.apply {
            viewmodel = this@MainFragment.viewModel
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        viewModel.start()
    }


    /************************************************************
     *  Private Functions
     ************************************************************/
    private fun initView() {
        // init view
        setupListAdapter()

        // init events
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null) {
            listAdapter = TasksAdapter(viewModel)

            val arrTitle = resources.getStringArray(R.array.arr_main_tech)
            listAdapter.list.addAll(arrTitle)
            binding.rvList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}