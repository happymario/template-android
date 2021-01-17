package com.victoria.bleled.app.main.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.victoria.bleled.databinding.FragmentMonthCalendarBinding
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [MonthCalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthCalendarFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentMonthCalendarBinding
    private lateinit var adapter: MonthCalendarAdapter

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

    private fun initView() {
        // view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        viewDataBinding.rvCalendar.layoutManager =
            GridLayoutManager(requireContext(), BaseCalendar.DAYS_OF_WEEK)

        adapter = MonthCalendarAdapter(requireContext(), object : MonthCalendarAdapterListener {
            override fun refreshCurrentMonth(calendar: Calendar) {
                val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
                viewDataBinding.tvMonth.text = sdf.format(calendar.time)
            }
        })
        viewDataBinding.rvCalendar.adapter = adapter
        adapter.notifyDataSetChanged()

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