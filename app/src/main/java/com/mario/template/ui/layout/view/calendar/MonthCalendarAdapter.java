package com.mario.template.ui.layout.view.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mario.template.databinding.ItemMonthCalendarBinding;

import java.util.Calendar;
import java.util.Date;


public class MonthCalendarAdapter extends RecyclerView.Adapter<MonthCalendarAdapter.ViewHolder> {
    private Context context = null;
    private BaseCalendar baseCalendar = new BaseCalendar();
    private Listener listener = null;

    public MonthCalendarAdapter(Context context, Listener listener) {
        baseCalendar.initBaseCalendar(calendar -> refreshView(calendar));

        this.context = context;
        this.listener = listener;
    }

    public void changeToPrevMonth() {
        baseCalendar.changeToPrevMonth(calendar -> refreshView(calendar));
    }

    public void changeToNextMonth() {
        baseCalendar.changeToNextMonth(calendar -> refreshView(calendar));
    }

    public void refreshView(Calendar calendar) {
        notifyDataSetChanged();
        if (listener != null) {
            listener.refreshCurrentMonth(calendar);
        }
    }

    @Override
    public int getItemCount() {
        return BaseCalendar.ROWS_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(baseCalendar, listener, position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        static ViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemMonthCalendarBinding binding = ItemMonthCalendarBinding.inflate(layoutInflater, parent, false);
            ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
            params.height = parent.getMeasuredHeight() / BaseCalendar.ROWS_OF_CALENDAR;
            binding.getRoot().setLayoutParams(params);
            return new ViewHolder(binding);
        }

        private ItemMonthCalendarBinding binding;

        ViewHolder(ItemMonthCalendarBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(BaseCalendar baseCalendar, Listener listener, int position) {
            if (position % BaseCalendar.DAYS_OF_WEEK == 0) {
                binding.tvDate.setTextColor(Color.parseColor("#ff1200"));
            } else {
                binding.tvDate.setTextColor(Color.parseColor("#676d6e"));
            }

            if (position < baseCalendar.prevMonthTailOffset || position >= (baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate)) {
                binding.tvDate.setAlpha(0.3f);
            } else {
                binding.tvDate.setAlpha(1f);
            }
            //binding.ivBg.setVisibility(View.GONE);
            //binding.ivCheck.setVisibility(View.GONE);

            // 현재날짜이면 배경 보이기
            String curDate = BaseCalendar.getCurrentDate();
            Date itemDate = baseCalendar.calendar.getTime();
            itemDate.setDate(baseCalendar.data.get(position));

//            if (CommonUtil.getDateFormat(itemDate, "yyyyMMdd").equals(curDate)) {
//                binding.ivBg.setVisibility(View.VISIBLE);
//            }

            binding.tvDate.setText(baseCalendar.data.get(position).toString());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Date date = baseCalendar.calendar.getTime();
                    date.setDate(baseCalendar.data.get(position));

                    if (listener != null) {
                        listener.selecteDate(date);
                    }
                }
            });
            binding.executePendingBindings();
        }
    }

    public interface Listener {
        void refreshCurrentMonth(Calendar calendar);

        void selecteDate(Date date);
    }
}
