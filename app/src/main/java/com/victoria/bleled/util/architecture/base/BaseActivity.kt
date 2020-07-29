package com.victoria.bleled.util.architecture.base

import android.app.Activity
import android.app.Dialog
import android.content.ContextWrapper
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R


open class BaseActivity : AppCompatActivity() {
    /************************************************************
     * Static & Global Members
     ************************************************************/


    /************************************************************
     * UI controls & Data members
     **********************************************************/
    private lateinit var mProgressDialog: Dialog
    private var mProgressCount = 0
    private var mIsShowProgress = false

    /************************************************************
     * Overrides
     ************************************************************/
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        createProgress()
    }

    /************************************************************
     * Event Handler
     ************************************************************/


    /************************************************************
     * Helpers
     ************************************************************/

    private fun createProgress() {
        mProgressDialog = Dialog(this, R.style.Loading_ProgressDialog)
        if (mProgressDialog.getWindow() != null) {
            mProgressDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
        }
        mProgressDialog.setCancelable(false)
        val linearLayout = LayoutInflater.from(this).inflate(R.layout.layout_loading_only, null)
        mProgressDialog.setContentView(linearLayout)
        val progressBar = mProgressDialog.findViewById<ProgressBar>(R.id.progressBar1)
        progressBar.isIndeterminate = true
        progressBar.indeterminateDrawable?.setColorFilter(
            resources.getColor(R.color.colorAccent),
            PorterDuff.Mode.MULTIPLY
        )
    }

    fun hideProgress() {
        if (mIsShowProgress == true) {
            if (mProgressCount != 0) {
                mProgressCount--
            } else {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    //get the Context object that was used to great the dialog
                    val context = (mProgressDialog.getContext() as ContextWrapper).baseContext

                    // if the Context used here was an activity AND it hasn't been finished or destroyed
                    // then dismiss it
                    if (context is Activity) {

                        // Api >=17
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!context.isFinishing && !context.isDestroyed) {
                                dismissWithTryCatch(mProgressDialog)
                            }
                        } else {
                            // Api < 17. Unfortunately cannot check for isDestroyed()
                            if (!context.isFinishing) {
                                dismissWithTryCatch(mProgressDialog)
                            }
                        }
                    } else
                    // if the Context used wasn't an Activity, then dismiss it too
                        dismissWithTryCatch(mProgressDialog)
                }
                //mProgressDialog = null;
                mIsShowProgress = false
            }
        }
    }

    fun showProgress() {
        if (true == mIsShowProgress) {
            mProgressCount++
            return
        }

        mProgressDialog.show()

        mIsShowProgress = true
        mProgressCount = 0
    }

    private fun dismissWithTryCatch(dialog: Dialog) {
        try {
            dialog.dismiss()
        } catch (e: IllegalArgumentException) {
            // Do nothing.
        } catch (e: Exception) {
            // Do nothing.
        } finally {
            //dialog = null;
        }
    }

    /************************************************************
     * Sub Classes
     ************************************************************/


}