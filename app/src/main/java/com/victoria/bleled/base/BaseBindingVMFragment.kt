package com.victoria.bleled.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.victoria.bleled.base.extensions.observeResource2
import com.victoria.bleled.base.internal.AppException
import com.victoria.bleled.base.internal.Resource

abstract class BaseBindingVMFragment<VB : ViewDataBinding, VM : BaseViewModel>(
) : BaseBindingFragment<VB>() {
    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        observeViewEvent()
        observeError()
        observeAlert()
    }

    /**
     * ViewModel 이벤트 처리
     */
    open fun observeViewModel() {}

    /**
     * View 의 직접적인 이벤트 처리
     */
    open fun observeViewEvent() {}


    inline fun <T : Resource<R>, R> LiveData<T>.observeResource(
        loadingView: View? = null,
        targetView: View? = null,
        crossinline block: (R) -> Unit,
    ) {
        observeResource2(viewLifecycleOwner, loadingView, targetView, block)
    }

    inline fun <T : Resource<R>, R> LiveData<T>.observeResourceWithEmpty(
        loadingView: View? = null,
        targetView: View? = null,
        emptyView: View? = null,
        crossinline block: (R) -> Unit,
    ) {
        // observeResource(viewLifecycleOwner, loadingView, targetView, block)
        observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                is Resource.Loading<*> -> {
                    loadingView?.visibility = View.VISIBLE
                    targetView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE
                }

                is Resource.Error<*> -> {
                    loadingView?.visibility = View.GONE
                    targetView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE
                }

                is Resource.Success<*> -> {
                    loadingView?.visibility = View.GONE

                    if (status.data() is List<*> && (status.data() as List<*>).isEmpty()) {
                        emptyView?.visibility = View.VISIBLE
                        targetView?.visibility = View.GONE
                    } else {
                        emptyView?.visibility = View.GONE
                        targetView?.visibility = View.VISIBLE
                    }

                    block.invoke(status.data!!)
                }
            }
        })
    }


    private fun observeError() {
        viewModel._error.observe(viewLifecycleOwner, Observer { exception ->
            when (exception) {
                is AppException.Network -> {
//                    AlertDialog(requireContext()).show {
//                        message("네트워크 오류.\n인터넷의 연결상태를 확인 후 다시 시도하세요.")
//                    }
                }

                is AppException.ServerHttp -> {
//                    AlertDialog(requireContext()).show {
//                        message("잘못된 방식의 요청입니다.\n서버 관리자에게 문의하세요.")
//                    }
                }

                is AppException.Unknown -> {
//                    AlertDialog(requireContext()).show {
//                        message("알 수 없는 에러:\n" + exception.throwable)
//                    }
                }

                else -> {}
            }
        })
    }

    private fun observeAlert() {
        viewModel._alert.observe(viewLifecycleOwner) { strings ->
//            AlertDialog(requireContext()).show {
//
//                if (strings is Int) {
//                    message(getString(strings))
//                } else if (strings is String) {
//                    message(strings)
//                } else {
//                    Timber.w("지원하지 않는 Alert 타입: ${strings}")
//                }
//            }
        }
    }
}