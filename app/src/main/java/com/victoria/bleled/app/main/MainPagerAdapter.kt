package com.victoria.bleled.app.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class MainPagerAdapter constructor(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val mFragmentTitles: MutableList<String> = ArrayList()
    private val hashMap: HashMap<Int, Fragment> = HashMap()

    fun setFragmentTitle(list: List<String>) {
        mFragmentTitles.clear()
        mFragmentTitles.addAll(list)
    }

    fun getFragmentTitle(position: Int): String {
        return mFragmentTitles[position]
    }

    fun getFragment(position: Int): Fragment? {
        return hashMap.get(position)
    }

    override fun getItemCount(): Int {
        return mFragmentTitles.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            1 -> TaskFragment.newInstance(1)
            2 -> TaskFragment.newInstance(2)
            else -> TaskFragment.newInstance(position)
        }
        hashMap.put(position, fragment)
        return fragment
    }

}