package com.victoria.bleled.base

import android.app.Activity
import android.app.Dialog
import android.content.ContextWrapper
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.victoria.bleled.R

abstract class BaseComposeActivity<VM : BaseViewModel> : ComponentActivity() {
    protected abstract val viewModel: VM

    /************************************************************
     * UI controls & Data members
     */
    private var mProgressDialog: Dialog? = null
    private var mProgressCount = 0
    private var mIsShowProgress = false

    @Composable
    abstract fun ComposeContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeContent()
        }
        createProgress()
    }

    /************************************************************
     * Event Handler
     */
    fun isShowingProgress(): Boolean {
        return mIsShowProgress
    }

    fun showProgress() {
        Log.d("showProgress=>", mProgressCount.toString())
        if (true == mIsShowProgress) {
            mProgressCount++
            return
        }
        mProgressDialog!!.show()
        mIsShowProgress = true
        mProgressCount = 0
    }

    fun hideProgress() {
        Log.d("hideProgress=>", mProgressCount.toString())
        if (mIsShowProgress == true) {
            if (mProgressCount > 1) {
                mProgressCount--
            } else {
                if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                    //get the Context object that was used to great the dialog
                    val context = (mProgressDialog!!.context as ContextWrapper).baseContext

                    // if the Context used here was an activity AND it hasn't been finished or destroyed
                    // then dismiss it
                    if (context is Activity) {

                        // Api >=17
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!context.isFinishing && !context.isDestroyed) {
                                dismissWithTryCatch(mProgressDialog!!)
                            }
                        } else {
                            // Api < 17. Unfortunately cannot check for isDestroyed()
                            if (!context.isFinishing) {
                                dismissWithTryCatch(mProgressDialog!!)
                            }
                        }
                    } else  // if the Context used wasn't an Activity, then dismiss it too
                        dismissWithTryCatch(mProgressDialog!!)
                }
                //mProgressDialog = null;
                mIsShowProgress = false
            }
        }
    }

    /************************************************************
     * Helpers
     */
    private fun createProgress() {
        mProgressDialog = Dialog(this, R.style.Loading_ProgressDialog)
        if (mProgressDialog!!.window != null) {
            mProgressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        mProgressDialog!!.setCancelable(false)
        val linearLayout: View = LayoutInflater.from(this).inflate(R.layout.view_loading_only, null)
        mProgressDialog!!.setContentView(linearLayout)
        val progressBar = mProgressDialog!!.findViewById<ProgressBar>(R.id.progressBar1)
        progressBar.isIndeterminate = true
        progressBar.indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.colorAccent),
            PorterDuff.Mode.MULTIPLY
        )
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