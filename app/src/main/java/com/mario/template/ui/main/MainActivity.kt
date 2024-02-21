package com.mario.template.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mario.template.R
import com.mario.template.base.BaseLayoutBindingActivity
import com.mario.template.databinding.ActivityMainBinding
import com.mario.template.helper.CommonHelper
import com.mario.template.helper.IntentShareHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@AndroidEntryPoint
class MainActivity : BaseLayoutBindingActivity<ActivityMainBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var pagerAdapter: MainPagerAdapter? = null
    private val viewModel by viewModels<MainViewModel>()
    private var isFinishAppWhenPressedBackKey = true
    private var isFinishDoing = false

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleSSLHandshake()

        initView()
        initViewModel()

        lifecycleScope.launch {
            viewModel.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)

        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.hint_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.isIconified = true
                searchView.onActionViewCollapsed()

                if (query != null) {
                    val fragment =
                        pagerAdapter?.getFragment(binding.viewpager.currentItem) as? TaskFragment
                    fragment?.getViewModel()?.searchTask(query)
                }
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.open()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /************************************************************
     *  Events
     ************************************************************/
    override fun onBackPressed() {
        if (isFinishAppWhenPressedBackKey) {
            finishWithMsg()
            return
        }
        super.onBackPressed()
    }

    fun onFab(view: View) {
        val bitmap = IntentShareHelper.captureBitmapFromView(binding.root)
        val shareUrl = IntentShareHelper.saveToSharedImage(this, bitmap)
        IntentShareHelper.shareEtc(this, shareUrl)
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    override fun initView() {
        super.initView()

        // views
        setupToolbar()
        setupNavigationDrawer()
        setupViewPager(binding.viewpager)

        // events
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = pagerAdapter?.getFragmentTitle(position)
        }.attach()

        binding.navView.getHeaderView(0).findViewById<View>(R.id.imageButton).setOnClickListener {
            goSetting()
        }
        binding.navView.setNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.menu_about -> {
                    goAbout()
                    false
                }

                R.id.menu_logout -> {
                    showLogoutDlg()
                    false
                }

                else -> {
                    true
                }
            }
        }
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val fragment =
                    pagerAdapter?.getFragment(binding.viewpager.currentItem) as? TaskFragment
                //fragment?.getViewModel()?.setPage(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun initViewModel() {
        binding.viewmodel = viewModel

        viewModel.userInfo.observe(this) { user ->
            if (user == null) {
                goLogin()
                return@observe
            }
            val parent = binding.navView.getHeaderView(0)
            parent.findViewById<TextView>(R.id.tv_nickname).text =
                user.name
            parent.findViewById<TextView>(R.id.tv_email).text = user.id
            //  "https://cdn.myholdem.io.s3.ap-northeast-2.amazonaws.com//75729800-eb9f-41c5-99af-11df13c9d2c8img_9126846222369274919.jpg"
//            setImageUrl(
//                parent.findViewById<ImageView>(R.id.iv_profile),
//                user.profile_url, R.drawable.profile
//            )
        }
    }

    private fun handleSSLHandshake() {
        try {

            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?, authType: String?,
                ) = Unit

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?, authType: String?,
                ) = Unit

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sc: SSLContext = SSLContext.getInstance("TLS")
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            HttpsURLConnection.setDefaultHostnameVerifier(object : HostnameVerifier {


                override fun verify(hostname: String?, session: SSLSession?): Boolean {


                    return true
                }
            })
        } catch (ignored: Exception) {


        }
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        val ab: ActionBar? = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab?.setDisplayHomeAsUpEnabled(true)

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { // 접혔을때
                binding.toolbar.visibility = View.VISIBLE
            } else {// 펴졌을때
                binding.toolbar.visibility = View.GONE
            }
        })
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        pagerAdapter = MainPagerAdapter(this)

        val titleArray = ArrayList<String>()
        titleArray.add(getString(R.string.tab_basic))
        titleArray.add(getString(R.string.tab_latest))
        titleArray.add(getString(R.string.tab_special))
        pagerAdapter?.setFragmentTitle(titleArray)

        viewPager.adapter = pagerAdapter
        //viewPager.isUserInputEnabled = false
    }

    private fun setupNavigationDrawer() {
        (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }

    private fun goSetting() {
//        val intent = Intent(this, SettingActivity::class.java)
//        startActivity(intent)
    }

    private fun goAbout() {
//        val intent = Intent(this, WebViewActivity::class.java).apply {
//            putExtra(Constants.ARG_TYPE, getString(R.string.about))
//            putExtra(Constants.ARG_DATA, Constants.URL_ABOUT)
//        }
//
//        startActivity(intent)
    }

    private fun goLogin() {
//        val intent = Intent(this, SigninActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        }
//        startActivity(intent)
    }

    private fun showLogoutDlg() {
//        AlertDialog.Builder(this)
//            .setMessage(getString(R.string.msg_logout))
//            .setPositiveButton(R.string.confirm) { dialog, which ->
//                lifecycleScope.launch {
//                    viewModel.logout()
//                }
//            }
//            .setNegativeButton(R.string.cancel) { dialog, which ->
//            }
//            .show()
    }

    private fun finishWithMsg() {
        if (!isFinishDoing) {
            isFinishDoing = true
            CommonHelper.showToast(this, R.string.app_finish_message)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                isFinishDoing = false
            }, 2000)
        } else {
            finish()
            instance = null
        }
    }

    /************************************************************
     *  Static & Global Members
     ************************************************************/
    companion object {
        @Volatile
        private var instance: MainActivity? = null

        /**
         * singleton 애플리케이션 객체를 얻는다
         *
         * @return singleton 애플리케이션 객체
         */
        val gInstance: MainActivity?
            get() = instance
    }
}
