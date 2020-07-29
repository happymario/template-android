package com.victoria.bleled.app.network.search

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R
import com.victoria.bleled.app.network.GithubServiceObserver
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.common.AppExecutors
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.local.db.GithubDb
import com.victoria.bleled.data.local.pref.PrefDataSourceImpl
import com.victoria.bleled.data.model.Repo
import com.victoria.bleled.data.remote.GithubService
import com.victoria.bleled.databinding.FragmentSearchBinding
import com.victoria.bleled.util.architecture.autoCleared
import com.victoria.bleled.util.architecture.base.BaseViewModelFactory
import com.victoria.bleled.util.architecture.base.BindingFragment
import com.victoria.bleled.util.architecture.remote.Resource
import com.victoria.bleled.util.architecture.remote.RetryCallback


class SearchFragment : BindingFragment<FragmentSearchBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var listener: OnFragmentInteractionListener? = null
    private var adapter by autoCleared<SearchListAdapter>()

    //private val searchViewModel: SearchViewModel = ViewModelProviders.of(this, BaseViewModelFactory{SearchViewModel()}).get(SearchViewModel::class.java)
    private val searchViewModel: SearchViewModel by viewModels {
        BaseViewModelFactory {
            SearchViewModel(
                DataRepository(
                    AppExecutors(),
                    GithubService.provideGithubService(),
                    GithubDb.provideDb(MyApplication.globalApplicationContext!!),
                    PrefDataSourceImpl.getInstance(activity!!)
                )
            )
        }
    }

    /************************************************************
     *  Overrrides
     ************************************************************/
    override fun getLayoutResId(): Int {
        return R.layout.fragment_search
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initViewModel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    private fun initUI() {
        binding.lifecycleOwner = this

        initView()
        initEvent()
    }

    private fun initView() {
        binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    // no need it
                }
            }
        })

        val rvAdapter = SearchListAdapter(context!!)
        rvAdapter.listener = object : SearchListAdapter.AdapterListener {
            override fun onClick(position: Int, info: Repo) {
                findNavController().navigate(
                    SearchFragmentDirections.showDetail(info.name)
                )
            }
        }
        binding.rvList.adapter = rvAdapter
        adapter = rvAdapter
    }

    private fun initEvent() {
        binding.cvRoot.setOnClickListener(View.OnClickListener {
            dismissKeyboard(binding.input.windowToken)
        })

        binding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun initViewModel() {
        searchViewModel.mainLiveData.observe(
            viewLifecycleOwner,
            object : GithubServiceObserver<List<Repo>>(activity!!, true) {
                override fun onChanged(t: Resource<List<Repo>>) {
                    if (t.data != null) {
                        adapter.setList(t.data!!)
                    }
                }
            })


        // binding setting
        binding.query = searchViewModel.query
        binding.searchResult = searchViewModel.mainLiveData
        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        // Dismiss keyboard
        dismissKeyboard(v.windowToken)
        searchViewModel.setQuery(query)
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    /************************************************************
     *  Subclasses
     ************************************************************/
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
