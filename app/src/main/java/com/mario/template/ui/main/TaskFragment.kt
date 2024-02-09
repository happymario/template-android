package com.mario.template.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mario.lib.base.architecture.EventObserver
import com.mario.template.R
import com.mario.template.base.BaseLayoutBindingFragment
import com.mario.template.databinding.FragmentTaskBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale


@AndroidEntryPoint
class TaskFragment : BaseLayoutBindingFragment<FragmentTaskBinding>() {
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
        get() = R.layout.fragment_task

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        if (arguments != null) {
            type = requireArguments().getInt("type")
        }

        val viewModel by viewModels<MainViewModel>()
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
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
        }
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
//        if (id == "menu") {
//            val popup = PopupMenu(requireContext(), view)
//            val inflater: MenuInflater = popup.menuInflater
//            inflater.inflate(R.menu.list_operation, popup.menu)
//            popup.setOnMenuItemClickListener {
//                CommonUtil.showNIToast(requireContext())
//                true
//            }
//            popup.show()
//        } else if (id == "animation") {
//            val intent = Intent(requireActivity(), AnimActivity::class.java)
//            startActivity(intent)
//        } else if (id == "camera") {
//            val intent = Intent(requireActivity(), CameraTestActivity::class.java)
//            startActivity(intent)
//        } else if (id == "gallery") {
//            val intent = Intent(requireActivity(), GallerySelectCropActivity::class.java)
//            intent.putExtra(Constants.ARG_TYPE, 1)
//            intent.putExtra(Constants.ARG_DATA, ArrayList<Gallary>())
//            startActivity(intent)
//        } else if (id == "download") {
//            val manager =
//                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//            val uri: Uri =
//                Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
//            val request: DownloadManager.Request = DownloadManager.Request(uri)
//                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//                .setTitle("fileName")
//                .setDescription("desc")
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                .setAllowedOverMetered(true)
//                .setAllowedOverRoaming(false)
//                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "fileName")
//            val reference: Long = manager.enqueue(request)
//            requireActivity().registerReceiver(object : BroadcastReceiver() {
//                override fun onReceive(context: Context?, intent: Intent?) {
//                    Timber.tag("Download").d("1%s", reference.toString())
//                }
//            }, IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED))
//        }
//
//        // recent
//        if (id == "workmanager") {
//            val workManager = WorkManager.getInstance(context)
//            workManager.cancelAllWork()
//
//            // 즉시 실행작업: WorkManager OneTime과 koroutine을 쓴다.
//            val notificationData = Data.Builder()
//                .putString(Constants.ARG_TYPE, "BootReceiver")
//                .build()
//            val workRequest1 =
//                OneTimeWorkRequestBuilder<SimpleWorker>()
//                    .setInputData(notificationData)
//                    .setBackoffCriteria(
//                        BackoffPolicy.LINEAR, 30000,
//                        java.util.concurrent.TimeUnit.MILLISECONDS
//                    )
//                    .build()
//            workManager.enqueue(workRequest1)
//
//            // 지연된 작업: 주기적인 작업의 최소 interval은 15분... workmanager주기작업은 꼭 지연이 있는데 왜냐면 시스템상태에 우선도를 부여하기때문이다.
//            val workRequest2 =
//                PeriodicWorkRequestBuilder<SimpleWorker>(
//                    15,
//                    java.util.concurrent.TimeUnit.MINUTES
//                ).setInitialDelay(
//                    1,
//                    java.util.concurrent.TimeUnit.MINUTES
//                ).build()
//            workManager.enqueue(workRequest2)
//        }
//        if (id == "alarmmanager") {
//            // 정확한 시간에 실행해야 한다면 AlarmManager
//            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
//
//            val intent = Intent(context, AlarmReceiver::class.java)
//            //val intent = Intent(context, MyAlarmService::class.java)
//            val pendingIntent =
//                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//            //val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
//            val triggerTime = (SystemClock.elapsedRealtime()
//                    + 10 * 1000)
//
//            alarmManager.cancel(pendingIntent)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                alarmManager.setExact(
//                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.set(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
//            }
//
//            // 앱끄기
//            CommonUtil.showToast(context, R.string.msg_workmanager_alarm)
//            MyApplication.globalApplicationContext?.finishAllActivityWithoutMain()
//            requireActivity().finish()
//        }
//        if (id == "pictureinpicture" || id == "videoplayer") {
//            val intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
//            startActivity(intent)
//        }
//        if (id == "compose") {
//            val intent = Intent(requireActivity(), ComposeMainActivity::class.java)
//            startActivity(intent)
//        }
//        if (id == "hilt") {
//            val intent = Intent(requireActivity(), HiltActivity::class.java)
//            startActivity(intent)
//        }
//
//        // special
//        if (id == "bluetooth") {
//            val intent = Intent(requireActivity(), BluetoothTestActivity::class.java)
//            startActivity(intent)
//        }
//        if (id == "calendar") {
//            val intent = Intent(requireActivity(), CalendarActivity::class.java)
//            startActivity(intent)
//        }
    }
}