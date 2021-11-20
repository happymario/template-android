package com.victoria.bleled.app.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class MainPagerAdapter constructor(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val mFragmentTitles: MutableList<String> = ArrayList()

    fun setFragmentTitle(list: List<String>) {
        mFragmentTitles.clear()
        mFragmentTitles.addAll(list)
    }

    fun getFragmentTitle(position: Int): String {
        return mFragmentTitles[position]
    }

    override fun getItemCount(): Int {
        return mFragmentTitles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> LatestFragment.newInstance()
            2 -> SpecialFragment.newInstance()
            else -> MainFragment.newInstance()
        }
    }

}