package com.victoria.bleled.util.arch.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.victoria.bleled.R;

public class BaseActivity extends AppCompatActivity {
    /************************************************************
     * Static & Global Members
     ************************************************************/


    /************************************************************
     * UI controls & Data members
     **********************************************************/
    private Dialog mProgressDialog;
    private int mProgressCount = 0;
    private boolean mIsShowProgress = false;

    /************************************************************
     * Overrides
     ************************************************************/
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        createProgress();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        createProgress();
    }

    /************************************************************
     * Event Handler
     ************************************************************/
    public boolean isShowingProgress() {
        return mIsShowProgress;
    }

    public void showProgress() {
        if (true == mIsShowProgress) {
            mProgressCount++;
            return;
        }

        mProgressDialog.show();

        mIsShowProgress = true;
        mProgressCount = 0;
    }

    public void hideProgress() {
        if (mIsShowProgress == true) {
            if (mProgressCount != 0) {
                mProgressCount--;
            } else {
                if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                    //get the Context object that was used to great the dialog
                    Context context = ((ContextWrapper) mProgressDialog.getContext()).getBaseContext();

                    // if the Context used here was an activity AND it hasn't been finished or destroyed
                    // then dismiss it
                    if (context instanceof Activity) {

                        // Api >=17
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                dismissWithTryCatch(mProgressDialog);
                            }
                        } else {
                            // Api < 17. Unfortunately cannot check for isDestroyed()
                            if (!((Activity) context).isFinishing()) {
                                dismissWithTryCatch(mProgressDialog);
                            }
                        }
                    } else
                        // if the Context used wasn't an Activity, then dismiss it too
                        dismissWithTryCatch(mProgressDialog);
                }
                //mProgressDialog = null;
                mIsShowProgress = false;
            }
        }
    }

    /************************************************************
     * Helpers
     ************************************************************/
    private void createProgress() {
        mProgressDialog = new Dialog(this, R.style.Loading_ProgressDialog);
        if (mProgressDialog.getWindow() != null) {
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        mProgressDialog.setCancelable(false);
        View linearLayout = LayoutInflater.from(this).inflate(R.layout.layout_loading_only, null);
        mProgressDialog.setContentView(linearLayout);
        ProgressBar progressBar = mProgressDialog.findViewById(R.id.progressBar1);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
    }

    private void dismissWithTryCatch(Dialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        } finally {
            //dialog = null;
        }
    }

    /************************************************************
     * Sub Classes
     ************************************************************/
}
