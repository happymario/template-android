package com.victoria.bleled.app.special.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daimajia.swipe.SwipeLayout
import com.victoria.bleled.R
import com.victoria.bleled.databinding.FragmentMonthCalendarBinding
import com.victoria.bleled.util.arch.AppExecutors
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * Use the [MonthCalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthCalendarFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentMonthCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding = FragmentMonthCalendarBinding.inflate(inflater, container, false)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onResume() {
        super.onResume()

        AppExecutors().mainThread.execute(Runnable {
            toggleLayout()
        })
    }

    fun toggleLayout() {
        viewDataBinding.godfather.open(false, SwipeLayout.DragEdge.Top)
    }

    private fun initView() {
        // view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        viewDataBinding.godfather.showMode = SwipeLayout.ShowMode.PullOut
        viewDataBinding.godfather.addDrag(
            SwipeLayout.DragEdge.Top,
            viewDataBinding.rvCalendar
        );
        viewDataBinding.godfather.addRevealListener(
            R.id.rv_calendar
        ) { child, edge, fraction, distance ->
            Timber.tag("MonthCalendar offset").d(fraction.toString())
        }

        // event

    }


    companion object {
        @JvmStatic
        fun newInstance() =
            MonthCalendarFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}