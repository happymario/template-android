package com.victoria.bleled.app.network.search

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.app.network.dagger.DaggerGithubComponent
import com.victoria.bleled.common.AppExecutors
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.local.db.GithubDb
import com.victoria.bleled.data.local.pref.PrefDataSourceImpl
import com.victoria.bleled.data.model.Repo
import com.victoria.bleled.data.remote.GithubService
import com.victoria.bleled.databinding.FragmentDetailListBinding
import com.victoria.bleled.util.architecture.autoCleared
import com.victoria.bleled.util.architecture.base.BaseViewModelFactory
import com.victoria.bleled.util.architecture.base.BindingFragment
import com.victoria.bleled.util.architecture.remote.RetryCallback
import javax.inject.Inject


class DetailListFragment : BindingFragment<FragmentDetailListBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @JvmStatic
        fun newInstance(query: String) =
            DetailListFragment().apply {
                arguments = Bundle().apply {
                    putString("query", query)
                }
            }
    }

    /************************************************************
     *  UI controls & Data members
     ************************************************************/

    private var listener: OnFragmentInteractionListener? = null
    private var adapter by autoCleared<DetailRepoAdapter>()

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

    @Inject
    lateinit var service: GithubService

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun getLayoutResId(): Int {
        return R.layout.fragment_detail_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var params = DetailListFragmentArgs.fromBundle(arguments!!)

        initUI()
        initViewModel()

        // service?.searchReposWithPage("Test", 1)
        searchViewModel.setQuery(params.query)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        initDagger()
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
     *  Event Handler
     ************************************************************/


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
                    // loadmore, no need in page adapter
                }
            }
        })

        val rvAdapter = DetailRepoAdapter()
        rvAdapter.listener = object : DetailRepoAdapter.AdapterListener {
            override fun onClick(position: Int, info: Repo) {
                searchViewModel.testPagedList(position)
            }
        }
        binding.rvList.adapter = rvAdapter
        adapter = rvAdapter
    }

    private fun initEvent() {
        // This callback will only be called when MyFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }
    }

    private fun initViewModel() {
        // viewmodel data
        searchViewModel.detailList.observe(viewLifecycleOwner, Observer { result ->
            if (result != null) {
                adapter.submitList(result)
            }
        })

        // binding datas
        binding.searchResult = searchViewModel.detailLiveData
        binding.query = searchViewModel.query
        binding.loadingMore = searchViewModel.detailLoadMore
        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }
    }


    private fun initDagger() {
        val compoent = DaggerGithubComponent.create()
        compoent.inject(this)
    }

    /************************************************************
     *  Subclasses
     ************************************************************/
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
