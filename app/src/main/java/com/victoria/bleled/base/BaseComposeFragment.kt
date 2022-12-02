package com.victoria.bleled.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.victoria.bleled.base.extensions.launchWithStarted
import kotlinx.coroutines.flow.StateFlow

abstract class BaseComposeFragment<VM : BaseViewModel>(

) : BaseFragment() {

    protected abstract val viewModel: VM

    @Composable
    abstract fun ComposeContent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? =
        ComposeView(requireContext()).apply {
            setContent {
                ComposeContent()
            }
        }

    inline fun <T : Any> LiveData<T?>.observe(crossinline f: (T?) -> Unit) {
        return this.observe(viewLifecycleOwner) {
            f(it)
        }
    }

    inline fun <T : Any> StateFlow<T>.launch(crossinline f: (T) -> Unit) {
        this.launchWithStarted(lifecycleScope, lifecycle, f)
    }
}