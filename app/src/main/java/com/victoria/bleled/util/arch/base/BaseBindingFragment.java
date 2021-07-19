package com.victoria.bleled.util.arch.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseBindingFragment<T extends ViewDataBinding> extends BaseFragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        root = binding.getRoot();
        binding.setLifecycleOwner(this);
        return root;
    }

    @Override
    public void onResume() {
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
