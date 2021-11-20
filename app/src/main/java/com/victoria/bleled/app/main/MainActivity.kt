package com.victoria.bleled.app.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.databinding.ActivityMainBinding
import com.victoria.bleled.service.billing.BillingDataSource
import com.victoria.bleled.util.arch.base.BaseBindingActivity


class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/


    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var pagerAdapter: MainPagerAdapter? = null
    private lateinit var billingDataSource: BillingDataSource

    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initPurchase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

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
     *  Helpers
     ************************************************************/
    override fun initView() {
        super.initView()

        setupToolbar()

        setupViewPager(binding.viewpager)
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = pagerAdapter?.getFragmentTitle(position)
        }.attach()

        binding.fab.setOnClickListener {
            if (billingDataSource == null || billingDataSource.isBillingSetupCompleted === false) {
                return@setOnClickListener
            }

            billingDataSource.launchBillingFlow(this, "basic_subscription")
        }
    }

    private fun initPurchase() {
        billingDataSource = BillingDataSource.getInstance(
            MyApplication.globalApplicationContext!!,
            arrayOf("pdt_1"),
            arrayOf("basic_subscription", "premium_subscription"),
            null
        )
        billingDataSource.observeNewPurchaseData().observe(this, { purchase ->
            if (purchase == null) {
                return@observe
            }

            val strings = purchase.skus
            for (i in strings.indices) {
                val inappId = strings[i]
                //billingDataSource.consumeInappPurchase(inappId);
            }
        })

        billingDataSource.isPurchased("basic_subscription").observe(this) { purchased ->
            if (!purchased) {
                // 서버상태가 구독구매인데 purchase false이면 cancelPay api호출
                Log.d("INAPP", "1");
            }
        }

        billingDataSource.resume()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        val ab: ActionBar? = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        pagerAdapter = MainPagerAdapter(this)

        val titleArray = ArrayList<String>()
        titleArray.add(getString(R.string.tab_main))
        titleArray.add(getString(R.string.tab_latest))
        titleArray.add(getString(R.string.tab_special))
        pagerAdapter?.setFragmentTitle(titleArray)

        viewPager.adapter = pagerAdapter
    }
}
