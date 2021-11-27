package com.victoria.bleled.app.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.victoria.bleled.R
import com.victoria.bleled.databinding.FragmentMainBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.EventObserver
import com.victoria.bleled.util.arch.base.BaseBindingFragment
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory
import timber.log.Timber
import java.util.*


class TaskFragment : BaseBindingFragment<FragmentMainBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @JvmStatic
        fun newInstance(type: Int) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putInt("type", type)
                }
            }
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }
    private lateinit var listAdapter: TaskAdapter
    private var type: Int = 0

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
        val root = super.onCreateView(inflater, container, savedInstanceState)

        if (arguments != null) {
            type = requireArguments().getInt("type")
        }
        binding.apply {
            viewmodel = this@TaskFragment.viewModel
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
        setupNavigation()

        // init events
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null) {
            listAdapter = TaskAdapter(viewModel)

            val arrIds =
                arrayOf(R.array.arr_main_tech, R.array.arr_recent_tech, R.array.arr_special_tech)
            val arrTitle = if (type < arrIds.size) resources.getStringArray(arrIds[type]) else {
                resources.getStringArray(arrIds[0])
            }
            listAdapter.list.addAll(arrTitle)
            binding.rvList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver { view ->
            openTaskDetails(view)
        })
    }

    private fun openTaskDetails(view: View) {
        val id = view.tag as String
        if (id.toLowerCase() == "menu") {
            val popup = PopupMenu(requireContext(), view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.menu_example, popup.menu)
            popup.setOnMenuItemClickListener {
                CommonUtil.showNIToast(requireContext())
                true
            }
            popup.show()
        }
    }
}