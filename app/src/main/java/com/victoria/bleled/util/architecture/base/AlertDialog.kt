package com.victoria.bleled.util.architecture.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.RelativeLayout

import androidx.databinding.DataBindingUtil
import com.victoria.bleled.R
import com.victoria.bleled.databinding.DialogAlertBinding


class AlertDialog(context: Context) :
    BaseDialog(context) {

    internal var isCancelable = true
    internal var isAdjustMode = false
    private var mBinding: DialogAlertBinding? = null
    private var mListener: ActionListener? = null

    class Builder(private val context: Context) {
        private val alertDialog: AlertDialog

        init {
            this.alertDialog = AlertDialog(context)
        }

        fun setTitle(titleResId: Int): Builder {
            return setTitle(context.getString(titleResId))
        }

        fun setTitle(title: String): Builder {
            alertDialog.setTitle(title)
            return this
        }

        fun setMessage(resId: Int): Builder {
            return setMessage(context.getText(resId).toString())
        }

        fun setMessageFont(dp: Int): Builder {
            this.alertDialog.setMessageFont(dp)
            return this
        }

        fun setMessage(message: String): Builder {
            this.alertDialog.setMessage(message)
            return this
        }

        fun setButtonsOrientation(orientation: Int): Builder {
            this.alertDialog.setButtonOrientation(orientation)
            return this
        }

        fun setPositiveButton(
            buttonResId: Int,
            onClickListener: DialogInterface.OnClickListener
        ): Builder {
            return setPositiveButton(context.getString(buttonResId), onClickListener)
        }

        fun setPositiveButton(
            btnName: String,
            onClickListener: DialogInterface.OnClickListener
        ): Builder {
            this.alertDialog.setPositiveButton(btnName, onClickListener)
            return this
        }

        fun setNegativeButton(i: Int, onClickListener: DialogInterface.OnClickListener): Builder {
            return setNegativeButton(this.context.getText(i), 0, onClickListener)
        }

        fun setNegativeButtonUsingJam(
            i: Int,
            jam: Int,
            onClickListener: DialogInterface.OnClickListener
        ): Builder {
            return setNegativeButton(this.context.getText(i), jam, onClickListener)
        }

        fun setNegativeButton(
            charSequence: CharSequence,
            jam: Int,
            onClickListener: DialogInterface.OnClickListener
        ): Builder {
            this.alertDialog.setNegativeButton(charSequence, jam, onClickListener)
            return this
        }

        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener): Builder {
            this.alertDialog.setOnCancelListener(onCancelListener)
            return this
        }

        fun setCancelable(isCancelable: Boolean): Builder {
            this.alertDialog.setCancelable(isCancelable)
            this.alertDialog.isCancelable = isCancelable
            return this
        }

        fun setCanceledOnTouchOutside(isCanceled: Boolean): Builder {
            this.alertDialog.setCanceledOnTouchOutside(isCanceled)
            this.alertDialog.isCancelable = isCanceled
            return this
        }

        fun setAdjustMode(isAdjust: Boolean, top: Int): Builder {
            this.alertDialog.setAdjustMode(isAdjust, top)
            return this
        }

        fun show() {
            if (this.context !is Activity || !this.context.isFinishing) {
                create().show()
            }
        }

        fun setActionListener(listener: ActionListener): Builder {
            this.alertDialog.setActionListener(listener)
            return this
        }

        fun create(): Dialog {
            return this.alertDialog
        }

    }

    override fun initUI() {
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        this.mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(getContext()),
            R.layout.dialog_alert,
            null,
            false
        )
        setContentView(mBinding!!.root)

        this.mBinding!!.rlyBg.setOnClickListener { v -> onBg() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun setTitle(title: String) {
        this.mBinding!!.tvTitle.visibility = View.VISIBLE
        this.mBinding!!.vDivider.visibility = View.VISIBLE
        this.mBinding!!.tvTitle.text = title
    }

    fun setMessage(i: Int) {
        setMessage(getContext().getText(i).toString())
    }

    fun setMessageFont(dp: Int) {
        this.mBinding!!.tvContent.textSize = dp.toFloat()
    }

    fun setButtonOrientation(i: Int) {
        if (i == LinearLayout.VERTICAL) {
            this.mBinding!!.llyBtns.orientation = LinearLayout.VERTICAL
            var layoutParams = this.mBinding!!.tvCancel.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            this.mBinding!!.tvCancel.layoutParams = layoutParams

            layoutParams = this.mBinding!!.tvConfirm.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            this.mBinding!!.tvConfirm.layoutParams = layoutParams
        } else if (i == LinearLayout.HORIZONTAL) {
            this.mBinding!!.llyBtns.orientation = LinearLayout.HORIZONTAL
        }
    }

    fun setMessage(message: String) {
        this.mBinding!!.tvContent.text = message
    }

    fun setPositiveButton(i: Int, onClickListener: DialogInterface.OnClickListener) {
        setPositiveButton(getContext().getText(i), onClickListener)
    }

    fun setAdjustMode(isAdjust: Boolean, top: Int) {
        isAdjustMode = isAdjust

        if (isAdjustMode == true) {
            val layoutParams = mBinding!!.llContent.layoutParams as RelativeLayout.LayoutParams
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT)
            layoutParams.topMargin = top
            mBinding!!.llContent.layoutParams = layoutParams
        } else {
            val layoutParams = mBinding!!.llContent.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            layoutParams.topMargin = 0
            mBinding!!.llContent.layoutParams = layoutParams
        }
    }

    fun setPositiveButton(
        charSequence: CharSequence,
        onClickListener: DialogInterface.OnClickListener?
    ) {
        this.mBinding!!.tvConfirm.visibility = View.VISIBLE
        this.mBinding!!.tvConfirm.text = charSequence
        this.mBinding!!.tvConfirm.setOnClickListener {
            onClickListener?.onClick(this@AlertDialog, DialogInterface.BUTTON_POSITIVE)
            dismiss()
        }

        refreshBtnLayout()
    }

    fun setActionListener(listener: ActionListener) {
        mListener = listener
    }

    fun setNegativeButton(i: Int, onClickListener: DialogInterface.OnClickListener) {
        setNegativeButton(getContext().getText(i), 0, onClickListener)
    }

    fun setNegativeButton(
        charSequence: CharSequence,
        jam: Int,
        onClickListener: DialogInterface.OnClickListener?
    ) {
        this.mBinding!!.tvCancel.visibility = View.VISIBLE
        this.mBinding!!.tvCancel.text = charSequence
        this.mBinding!!.tvCancel.setOnClickListener {
            if (jam > 0) {
                if (!mBinding!!.tvCancel.isSelected) {
                    //                        String jamCount = String.format(Locale.getDefault(), getContext().getString(R.string.unit_jam), jam);
                    //                        mBinding.tvCancel.setText(jamCount);
                    mBinding!!.tvCancel.isSelected = true
                } else {
                    onClickListener?.onClick(this@AlertDialog, DialogInterface.BUTTON_NEGATIVE)
                    dismiss()
                }
            } else {
                onClickListener?.onClick(this@AlertDialog, DialogInterface.BUTTON_NEGATIVE)
                dismiss()
            }
        }

        refreshBtnLayout()
    }

    private fun refreshBtnLayout() {
        val confirmVisible = this.mBinding!!.tvConfirm.visibility == View.VISIBLE
        val cancelVisible = this.mBinding!!.tvCancel.visibility == View.VISIBLE

        this.mBinding!!.llyBtns.visibility = View.GONE
        if (cancelVisible && confirmVisible) {
            this.mBinding!!.tvCancel.setBackgroundResource(R.drawable.xml_round_btn_cancel)
            this.mBinding!!.tvConfirm.setBackgroundResource(R.drawable.xml_round_btn_ok)
            this.mBinding!!.llyBtns.visibility = View.VISIBLE
        } else if (confirmVisible) {
            this.mBinding!!.tvConfirm.setBackgroundResource(R.drawable.xml_round_btn_ok)
            this.mBinding!!.llyBtns.visibility = View.VISIBLE
        }
    }

    internal fun onBg() {
        if (isCancelable == true) {
            if (mListener != null) {
                mListener!!.onBG()
            }
            dismiss()
        }
    }

    interface ActionListener {
        fun onBG()
    }
}

