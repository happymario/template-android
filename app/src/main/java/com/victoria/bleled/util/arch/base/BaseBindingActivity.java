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
        binding.setLifecycleOwner(this);
        super.setContentView(this.binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // updateLang
        binding.invalidateAll();
    }

    /************************************************************
     *  Event Handler
     ************************************************************/


    /************************************************************
     *  Helpers
     ************************************************************/
    protected void initView() {
        binding.setLifecycleOwner(this);
    }

    /************************************************************
     *  Sub Classes
     ************************************************************/

}
