package com.victoria.bleled.util.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.victoria.bleled.R;

import java.util.ArrayList;

public class ZeroHintSpinnerAdapter extends ArrayAdapter<String> {

    private boolean mIsZeroHint = true;

    public ZeroHintSpinnerAdapter(Context context, int layout_id, ArrayList arrAge) {
        super(context, layout_id, arrAge);
    }

    public ZeroHintSpinnerAdapter(Context context, int layout_id, String[] arrAge) {
        super(context, layout_id, arrAge);
    }

    public void setZeroHint(boolean hint) {
        mIsZeroHint = hint;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if (position == 0 && mIsZeroHint == true) {
            ((TextView) v).setTextColor(getContext().getResources().getColorStateList(R.color.color_black));
        } else {
            ((TextView) v).setTextColor(getContext().getResources().getColorStateList(R.color.color_black));
        }
        ((TextView) v).setTextSize(12);
        return v;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);

        ((TextView) v).setTextColor(
                getContext().getResources().getColorStateList(R.color.color_black)
        );

        return v;
    }
}