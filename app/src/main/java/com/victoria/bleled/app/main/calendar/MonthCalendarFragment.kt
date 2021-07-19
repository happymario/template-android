package com.victoria.bleled.app.main.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daimajia.swipe.SwipeLayout
import com.victoria.bleled.R
import com.victoria.bleled.databinding.FragmentMonthCalendarBinding
import com.victoria.bleled.util.arch.AppExecutors
import kotlinx.android.synthetic.main.fragment_month_calendar.view.*


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
            viewDataBinding.godfather.rv_calendar
        );
        viewDataBinding.godfather.addRevealListener(
            R.id.rv_calendar
        ) { child, edge, fraction, distance ->
            val star = viewDataBinding.rvCalendar
            val d = child!!.height / 2 - star.height / 2.toFloat()

            Log.d("MonthCalendar offset", fraction.toString())
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