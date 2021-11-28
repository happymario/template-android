package com.victoria.bleled.app.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.victoria.bleled.R
import com.victoria.bleled.app.essential.CameraTestActivity
import com.victoria.bleled.app.essential.anim.AnimActivity
import com.victoria.bleled.app.essential.gallery.GallerySelectCropActivity
import com.victoria.bleled.app.recent.SimpleWorker
import com.victoria.bleled.app.recent.VideoPlayerActivity
import com.victoria.bleled.app.special.bluetooth.BluetoothTestActivity
import com.victoria.bleled.app.special.calendar.CalendarActivity
import com.victoria.bleled.app.special.imagesearch.ImageSearchActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.databinding.FragmentMainBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.EventObserver
import com.victoria.bleled.util.arch.base.BaseBindingFragment
import com.victoria.bleled.util.feature.gallary.Gallary
import com.victoria.bleled.util.kotlin_ext.getViewModelFactory
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


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
        val tag = view.tag as String
        val id = tag.lowercase(Locale.getDefault())
        val context = requireContext()

        // essential
        if (id == "menu") {
            val popup = PopupMenu(requireContext(), view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.menu_example, popup.menu)
            popup.setOnMenuItemClickListener {
                CommonUtil.showNIToast(requireContext())
                true
            }
            popup.show()
        } else if (id == "animation") {
            val intent = Intent(requireActivity(), AnimActivity::class.java)
            startActivity(intent)
        } else if (id == "camera") {
            val intent = Intent(requireActivity(), CameraTestActivity::class.java)
            startActivity(intent)
        } else if (id == "gallery") {
            val intent = Intent(requireActivity(), GallerySelectCropActivity::class.java)
            intent.putExtra(Constants.ARG_TYPE, 1)
            intent.putExtra(Constants.ARG_DATA, ArrayList<Gallary>())
            startActivity(intent)
        }


        // recent
        if (id == "workmanager") {
            // 주기적인 작업의 최소 interval은 15분... 왜냐면 workmanager는 시스템상태에 우선도를 부여하기때문이다.
            val workRequest =
                PeriodicWorkRequestBuilder<SimpleWorker>(15, TimeUnit.MINUTES).setInitialDelay(
                    1,
                    TimeUnit.MINUTES
                ).build()
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()
            workManager.enqueue(workRequest)

            CommonUtil.showToast(context, R.string.msg_workmanager_alarm)
            requireActivity().finish()
        }
        if (id == "pictureinpicture" || id == "videoplayer") {
            val intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
            startActivity(intent)
        }

        // special
        if (id == "bluetooth") {
            val intent = Intent(requireActivity(), BluetoothTestActivity::class.java)
            startActivity(intent)
        }
        if (id == "calendar") {
            val intent = Intent(requireActivity(), CalendarActivity::class.java)
            startActivity(intent)
        }
        if (id == "imagesearch") {
            val intent = Intent(requireActivity(), ImageSearchActivity::class.java)
            startActivity(intent)
        }

    }
}