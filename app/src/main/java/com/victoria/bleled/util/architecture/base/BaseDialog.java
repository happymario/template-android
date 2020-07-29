package com.victoria.bleled.util.architecture.base;


import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.victoria.bleled.R;

public abstract class BaseDialog extends Dialog {

    public Context context;

    public abstract void onCreateView();

    public BaseDialog(Context context) {
        super(context, R.style.DialogCustomTheme);
        this.context = context;
        onCreateView();
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        onCreateView();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        onCreateView();
    }
}
