package com.victoria.bleled.util.arch.base;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseBindingActivity<T extends ViewDataBinding> extends BaseActivity {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    public T binding;


    /************************************************************
     *  Overrides
     ************************************************************/
    @Override
    public void setContentView(int layoutResID) {
        this.binding = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, true);
        super.setContentView(this.binding.getRoot());
    }


    /************************************************************
     *  Event Handler
     ************************************************************/


    /************************************************************
     *  Helpers
     ************************************************************/


    /************************************************************
     *  Sub Classes
     ************************************************************/

}
