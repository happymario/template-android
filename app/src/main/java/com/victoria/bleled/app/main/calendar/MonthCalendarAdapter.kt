package com.victoria.bleled.app.main.calendar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.databinding.ItemMonthCalendarBinding
import java.util.*

class MonthCalendarAdapter constructor(
    private val context: Context,
    private var listener: MonthCalendarAdapterListener
) :
    RecyclerView.Adapter<MonthCalendarAdapter.ViewHolder>() {

    val baseCalendar = BaseCalendar()

    init {
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }

    override fun getItemCount(): Int {
        return BaseCalendar.ROWS_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(baseCalendar, position)
    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }

    fun refreshView(calendar: Calendar) {
        notifyDataSetChanged()
        listener.refreshCurrentMonth(calendar)
    }

    class ViewHolder private constructor(val binding: ItemMonthCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(baseCalendar: BaseCalendar, position: Int) {
            if (position % BaseCalendar.DAYS_OF_WEEK == 0) {
                binding.tvDate.setTextColor(Color.parseColor("#ff1200"))
            } else {
                binding.tvDate.setTextColor(Color.parseColor("#676d6e"))
            }

            if (position < baseCalendar.prevMonthTailOffset || position >= (baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate)) {
                binding.tvDate.alpha = 0.3f
            } else {
                binding.tvDate.alpha = 1f
            }

            binding.tvDate.text = baseCalendar.data[position].toString()

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMonthCalendarBinding.inflate(layoutInflater, parent, false)
                val params = binding.root.layoutParams
                params.height = parent.measuredHeight / BaseCalendar.ROWS_OF_CALENDAR
                binding.root.layoutParams = params
                return ViewHolder(binding)
            }
        }
    }
}

interface MonthCalendarAdapterListener {
    fun refreshCurrentMonth(calendar: Calendar)
}