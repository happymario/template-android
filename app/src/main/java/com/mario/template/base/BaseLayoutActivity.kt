package com.mario.template.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.mario.lib.base.util.LocaleUtil
import com.mario.template.R
import java.util.Locale

open class BaseLayoutActivity : AppCompatActivity() {
    /************************************************************
     * Static & Global Members
     */
    /************************************************************
     * UI controls & Data members
     */
    private var mProgressDialog: Dialog? = null
    private var mProgressCount = 0

    /************************************************************
     * Event Handler
     */
    var isShowingProgress = false
        private set

    /************************************************************
     * Overrides
     */
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        createProgress()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        createProgress()
    }

    override fun attachBaseContext(base: Context) {
        val locale = Locale.getDefault().language
        super.attachBaseContext(LocaleUtil.setLocale(base, locale))
    }

    fun showProgress() {
        Log.d("showProgress=>", mProgressCount.toString())
        if (true == isShowingProgress) {
            mProgressCount++
            return
        }
        mProgressDialog!!.show()
        isShowingProgress = true
        mProgressCount = 0
    }

    fun hideProgress() {
        Log.d("hideProgress=>", mProgressCount.toString())
        if (isShowingProgress == true) {
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
                isShowingProgress = false
            }
        }
    }


    fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
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
        val linearLayout = LayoutInflater.from(this).inflate(R.layout.view_progress, null)
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
     */
}