package com.victoria.bleled.app.network

import android.net.Uri
import com.victoria.bleled.R
import com.victoria.bleled.app.network.search.DetailListFragment
import com.victoria.bleled.app.network.search.SearchFragment
import com.victoria.bleled.databinding.ActivityGithubBinding
import com.victoria.bleled.util.architecture.base.BindingActivity

class GithubActivity : BindingActivity<ActivityGithubBinding>(),
    DetailListFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    override fun getLayout(): Int {
        return R.layout.activity_github
    }

    override fun initUI() {

    }

    override fun onFragmentInteraction(uri: Uri) {

    }


    /************************************************************
     *  Event Handler
     ************************************************************/


    /************************************************************
     *  Helpers
     ************************************************************/


    /************************************************************
     *  Networking
     ************************************************************/


    /************************************************************
     *  SubClasses
     ************************************************************/
}
