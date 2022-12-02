package com.victoria.bleled.app.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.work.*
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.app.essential.CameraTestActivity
import com.victoria.bleled.app.essential.anim.AnimActivity
import com.victoria.bleled.app.essential.gallery.GallerySelectCropActivity
import com.victoria.bleled.app.recent.VideoPlayerActivity
import com.victoria.bleled.app.recent.background.AlarmReceiver
import com.victoria.bleled.app.recent.background.SimpleWorker
import com.victoria.bleled.app.recent.compose.ComposeMainActivity
import com.victoria.bleled.app.recent.hilt.HiltActivity
import com.victoria.bleled.app.special.bluetooth.BluetoothTestActivity
import com.victoria.bleled.app.special.calendar.CalendarActivity
import com.victoria.bleled.base.BaseBindingFragment
import com.victoria.bleled.base.extensions.getViewModelFactory
import com.victoria.bleled.base.internal.EventObserver
import com.victoria.bleled.common.Constants
import com.victoria.bleled.databinding.FragmentMainBinding
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.feature.gallary.Gallary
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
    private lateinit var viewModel: MainViewModel
    private lateinit var listAdapter: TaskAdapter
    private var type: Int = 0

    /************************************************************
     *  Overrides
     ************************************************************/
    override val layoutId: Int
        get() = R.layout.fragment_main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        if (arguments != null) {
            type = requireArguments().getInt("type")
        }

//        val viewModel by activityViewModels<MainViewModel> {
//            getViewModelFactory()
//        }
        val viewModel by viewModels<MainViewModel> {
            getViewModelFactory()
        }
        this.viewModel = viewModel
        binding.apply {
            viewmodel = this@TaskFragment.viewModel
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        this.viewModel.setPage(type)
    }

    /************************************************************
     *  public Functions
     ************************************************************/
    fun getViewModel(): MainViewModel {
        return viewModel
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
            inflater.inflate(R.menu.list_operation, popup.menu)
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
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()

            // 즉시 실행작업: WorkManager OneTime과 koroutine을 쓴다.
            val notificationData = Data.Builder()
                .putString(Constants.ARG_TYPE, "BootReceiver")
                .build()
            val workRequest1 =
                OneTimeWorkRequestBuilder<SimpleWorker>()
                    .setInputData(notificationData)
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR, 30000,
                        java.util.concurrent.TimeUnit.MILLISECONDS
                    )
                    .build()
            workManager.enqueue(workRequest1)

            // 지연된 작업: 주기적인 작업의 최소 interval은 15분... workmanager주기작업은 꼭 지연이 있는데 왜냐면 시스템상태에 우선도를 부여하기때문이다.
            val workRequest2 =
                PeriodicWorkRequestBuilder<SimpleWorker>(
                    15,
                    java.util.concurrent.TimeUnit.MINUTES
                ).setInitialDelay(
                    1,
                    java.util.concurrent.TimeUnit.MINUTES
                ).build()
            workManager.enqueue(workRequest2)
        }
        if (id == "alarmmanager") {
            // 정확한 시간에 실행해야 한다면 AlarmManager
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java)
            //val intent = Intent(context, MyAlarmService::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            //val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
            val triggerTime = (SystemClock.elapsedRealtime()
                    + 10 * 1000)

            alarmManager.cancel(pendingIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }

            // 앱끄기
            CommonUtil.showToast(context, R.string.msg_workmanager_alarm)
            MyApplication.globalApplicationContext?.finishAllActivityWithoutMain()
            requireActivity().finish()
        }
        if (id == "pictureinpicture" || id == "videoplayer") {
            val intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
            startActivity(intent)
        }
        if (id == "compose") {
            val intent = Intent(requireActivity(), ComposeMainActivity::class.java)
            startActivity(intent)
        }
        if (id == "hilt") {
            val intent = Intent(requireActivity(), HiltActivity::class.java)
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
    }
}