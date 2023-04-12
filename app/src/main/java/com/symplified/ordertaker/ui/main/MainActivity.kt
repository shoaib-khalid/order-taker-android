package com.symplified.ordertaker.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.ActivityMainBinding
import com.symplified.ordertaker.models.stores.BusinessType
import com.symplified.ordertaker.ui.login.LoginActivity
import com.symplified.ordertaker.ui.main.menu_and_cart.PaymentActivity
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MainViewModel
import com.symplified.ordertaker.viewmodels.OrderResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_logout), drawerLayout
        )

        val navView: NavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
                    as NavHostFragment
        navController = navHostFragment.navController
        val inflater = navController.navInflater

        val headerView = navView.getHeaderView(0)
        val navHeaderTitle: TextView = headerView.findViewById(R.id.nav_header_title)
        val navHeaderSubtitle: TextView = headerView.findViewById(R.id.nav_header_subtitle)
        val navHeaderImage: ImageView = headerView.findViewById(R.id.imageView)

        mainViewModel.user.observe(this) { user ->
            if (user != null) {
                navHeaderTitle.text = user.storeName
                navHeaderSubtitle.text = user.name

                navController.graph = inflater.inflate(
                    if (user.businessType == BusinessType.ECOMMERCE)
                        R.navigation.ecommerce_store_navigation
                    else R.navigation.fnb_store_navigation
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                savedInstanceState?.getInt(NAV_ID, -1)?.let { currentNavId ->
                    Log.d("navigation", "Current nav id: $currentNavId")
                    if (currentNavId != -1) {
                        navController.navigate(currentNavId)
                    }
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        mainViewModel.headerImageUrl.observe(this) { assetUrl ->
            Glide.with(this).load(assetUrl).into(navHeaderImage)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.orderResult.collect { orderResult ->
                    if (orderResult is OrderResult.Success
                        || orderResult is OrderResult.Failure
                    ) {
                        showSnackBar(orderResult.message)

                        if (orderResult is OrderResult.Success && orderResult.paymentUrl != null) {
                            startActivity(Intent(
                                this@MainActivity,
                                PaymentActivity::class.java
                            ).apply {
                                putExtra(PaymentActivity.URL, orderResult.paymentUrl)
                            })
                        }
                    }
                }
            }
        }

//        startActivity(Intent(
//            this@MainActivity,
//            PaymentActivity::class.java
//        ).apply {
//            putExtra(PaymentActivity.URL, "https://paymentv2.dev-my.symplified.ai/online-payment?storeId=c9315221-a003-4830-9e28-c26c3d044dff&orderId=21ef207f-9605-4468-92f8-996676d50380")
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_reload -> {
            item.isVisible = false
            Snackbar.make(binding.root, "Reloading data. Please wait", Snackbar.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.IO).launch {
                val successResponses: MutableList<Boolean> = mutableListOf()
                App.userRepository.getStoreId()?.let { storeId ->
                    successResponses.add(App.zoneRepository.fetchZonesAndTables(storeId))
                    successResponses.add(App.productRepository.fetchCategories(storeId))
                    successResponses.add(App.productRepository.fetchProducts(storeId))
                    successResponses.add(App.paymentChannelRepository.fetchPaymentChannels())
                    successResponses.add(App.productRepository.fetchBestSellers(storeId))
                    successResponses.add(App.productRepository.fetchOpenItemProducts(storeId))
                }

                withContext(Dispatchers.Main) {
                    showSnackBar(
                        if (successResponses.any { !it })
                            "An error occurred while reloading data. Please try again"
                        else
                            "All data successfully reloaded",
                    )
                    item.isVisible = true
                }
            }
            true
        }
        R.id.action_logout -> {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.action_logout_confirmation))
                .setPositiveButton(getString(R.string.yes)) { _, _ -> mainViewModel.logout() }
                .setNegativeButton(getString(R.string.no), null)
                .show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NAV_ID, navController.currentDestination?.id ?: -1)
    }

    private fun showSnackBar(message: String) = Snackbar.make(
        binding.root,
        message,
        Snackbar.LENGTH_SHORT
    ).show()

    companion object {
        private const val NAV_ID = "nav_id"
    }
}