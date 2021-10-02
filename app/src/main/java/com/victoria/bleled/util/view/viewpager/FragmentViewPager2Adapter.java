package com.victoria.bleled.util.view.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentViewPager2Adapter extends FragmentStateAdapter {
    private List<Fragment> fragments = new ArrayList<>();

    public FragmentViewPager2Adapter(FragmentManager fragment, Lifecycle lifecycle) {
        super(fragment, lifecycle);
    }

    public void setFragments(List<Fragment> list) {
        if (list != null) {
            this.fragments.clear();
            this.fragments.addAll(list);
            notifyDataSetChanged();
        }
    }

    public Fragment getFragment(int idx) {
        if (idx >= fragments.size()) {
            return null;
        }
        return fragments.get(idx);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (this.fragments.size() <= position) {
            return null;
        }
        return this.fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
