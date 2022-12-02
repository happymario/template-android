package com.victoria.bleled.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment : Fragment() {
    @JvmField
    protected var root: View? = null

    /************************************************************
     * Overrides
     */
    abstract val layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(layoutId, container, false)
        return root
    }

    protected fun replaceFragment(fragment: Fragment, frameId: Int, tag: String? = null) {
        if (tag != null) {
            val frag = childFragmentManager.findFragmentByTag(tag)
            if (frag != null && frag.isAdded) {
                Timber.d("tag:(${tag})는 이미 그려진 상태:")
                return
            }
        }

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(frameId, fragment, tag)
        transaction.commitAllowingStateLoss()
    }
}