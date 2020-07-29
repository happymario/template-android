package com.victoria.bleled.util.architecture.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.victoria.bleled.R;
import com.victoria.bleled.databinding.DialogAlertBinding;

public class AlertDialog extends BaseDialog {

    boolean isCancelable = true;
    boolean isAdjustMode = false;
    private DialogAlertBinding mBinding;
    private ActionListener mListener;

    public static class Builder {
        private AlertDialog alertDialog;
        private Context context;

        public Builder(Context context) {
            this.context = context;
            this.alertDialog = new AlertDialog(context);
        }

        public Builder setTitle(int titleResId) {
            return setTitle(context.getString(titleResId));
        }

        public Builder setTitle(String title) {
            alertDialog.setTitle(title);
            return this;
        }

        public Builder setMessage(int resId) {
            return setMessage(context.getText(resId).toString());
        }

        public Builder setMessageFont(int dp) {
            this.alertDialog.setMessageFont(dp);
            return this;
        }

        public Builder setMessage(String message) {
            this.alertDialog.setMessage(message);
            return this;
        }

        public Builder setButtonsOrientation(int orientation) {
            this.alertDialog.setButtonOrientation(orientation);
            return this;
        }

        public Builder setPositiveButton(int buttonResId, OnClickListener onClickListener) {
            return setPositiveButton(context.getString(buttonResId), onClickListener);
        }

        public Builder setPositiveButton(String btnName, OnClickListener onClickListener) {
            this.alertDialog.setPositiveButton(btnName, onClickListener);
            return this;
        }

        public Builder setNegativeButton(int i, OnClickListener onClickListener) {
            return setNegativeButton(this.context.getText(i), 0, onClickListener);
        }

        public Builder setNegativeButtonUsingJam(int i, int jam, OnClickListener onClickListener) {
            return setNegativeButton(this.context.getText(i), jam, onClickListener);
        }

        public Builder setNegativeButton(CharSequence charSequence, int jam, OnClickListener onClickListener) {
            this.alertDialog.setNegativeButton(charSequence, jam, onClickListener);
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.alertDialog.setOnCancelListener(onCancelListener);
            return this;
        }

        public Builder setCancelable(boolean isCancelable) {
            this.alertDialog.setCancelable(isCancelable);
            this.alertDialog.isCancelable = isCancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean isCanceled) {
            this.alertDialog.setCanceledOnTouchOutside(isCanceled);
            this.alertDialog.isCancelable = isCanceled;
            return this;
        }

        public Builder setAdjustMode(boolean isAdjust, int top) {
            this.alertDialog.setAdjustMode(isAdjust, top);
            return this;
        }

        public void show() {
            if (!(this.context instanceof Activity) || !((Activity) this.context).isFinishing()) {
                create().show();
            }
        }

        public Builder setActionListener(ActionListener listener) {
            this.alertDialog.setActionListener(listener);
            return this;
        }

        public Dialog create() {
            return this.alertDialog;
        }

    }

    public AlertDialog(Context context) {
        super(context, R.style.DialogCustomTheme);
    }

    @Override
    public void onCreateView() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_alert, null, false);
        setContentView(mBinding.getRoot());

        this.mBinding.rlyBg.setOnClickListener(v -> {
            onBg();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setTitle(String title) {
        this.mBinding.tvTitle.setVisibility(View.VISIBLE);
        this.mBinding.vDivider.setVisibility(View.VISIBLE);
        this.mBinding.tvTitle.setText(title);
    }

    public void setMessage(int i) {
        setMessage(getContext().getText(i).toString());
    }

    public void setMessageFont(int dp) {
        this.mBinding.tvContent.setTextSize(dp);
    }

    public void setButtonOrientation(int i) {
        if (i == LinearLayout.VERTICAL) {
            this.mBinding.llyBtns.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mBinding.tvCancel.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            this.mBinding.tvCancel.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) this.mBinding.tvConfirm.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            this.mBinding.tvConfirm.setLayoutParams(layoutParams);
        } else if (i == LinearLayout.HORIZONTAL) {
            this.mBinding.llyBtns.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    public void setMessage(String message) {
        this.mBinding.tvContent.setText(message);
    }

    public void setPositiveButton(int i, OnClickListener onClickListener) {
        setPositiveButton(getContext().getText(i), onClickListener);
    }

    public void setAdjustMode(boolean isAdjust, int top) {
        isAdjustMode = isAdjust;

        if (isAdjustMode == true) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.llContent.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.topMargin = top;
            mBinding.llContent.setLayoutParams(layoutParams);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.llContent.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.topMargin = 0;
            mBinding.llContent.setLayoutParams(layoutParams);
        }
    }

    public void setPositiveButton(CharSequence charSequence, final OnClickListener onClickListener) {
        this.mBinding.tvConfirm.setVisibility(View.VISIBLE);
        this.mBinding.tvConfirm.setText(charSequence);
        this.mBinding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(AlertDialog.this, AlertDialog.BUTTON_POSITIVE);
                }
                dismiss();
            }
        });

        refreshBtnLayout();
    }

    public void setActionListener(ActionListener listener) {
        mListener = listener;
    }

    public void setNegativeButton(int i, OnClickListener onClickListener) {
        setNegativeButton(getContext().getText(i), 0, onClickListener);
    }

    public void setNegativeButton(CharSequence charSequence, int jam, final OnClickListener onClickListener) {
        this.mBinding.tvCancel.setVisibility(View.VISIBLE);
        this.mBinding.tvCancel.setText(charSequence);
        this.mBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jam > 0) {
                    if (!mBinding.tvCancel.isSelected()) {
//                        String jamCount = String.format(Locale.getDefault(), getContext().getString(R.string.unit_jam), jam);
//                        mBinding.tvCancel.setText(jamCount);
                        mBinding.tvCancel.setSelected(true);
                    } else {
                        if (onClickListener != null) {
                            onClickListener.onClick(AlertDialog.this, AlertDialog.BUTTON_NEGATIVE);
                        }
                        dismiss();
                    }
                } else {
                    if (onClickListener != null) {
                        onClickListener.onClick(AlertDialog.this, AlertDialog.BUTTON_NEGATIVE);
                    }
                    dismiss();
                }
            }
        });

        refreshBtnLayout();
    }

    private void refreshBtnLayout() {
        Boolean confirmVisible = this.mBinding.tvConfirm.getVisibility() == View.VISIBLE;
        Boolean cancelVisible = this.mBinding.tvCancel.getVisibility() == View.VISIBLE;

        this.mBinding.llyBtns.setVisibility(View.GONE);
        if (cancelVisible && confirmVisible) {
            this.mBinding.tvCancel.setBackgroundResource(R.drawable.xml_round_btn_ok);
            this.mBinding.tvConfirm.setBackgroundResource(R.drawable.xml_round_btn_cancel);
            this.mBinding.llyBtns.setVisibility(View.VISIBLE);
        } else if (confirmVisible) {
            this.mBinding.tvConfirm.setBackgroundResource(R.drawable.xml_round_btn_ok);
            this.mBinding.llyBtns.setVisibility(View.VISIBLE);
        }
    }

    void onBg() {
        if (isCancelable == true) {
            if (mListener != null) {
                mListener.onBG();
            }
            dismiss();
        }
    }

    public interface ActionListener {
        void onBG();
    }
}

