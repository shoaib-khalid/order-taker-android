package com.symplified.easydukanpos.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
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
import com.symplified.easydukanpos.App
import com.symplified.easydukanpos.BuildConfig
import com.symplified.easydukanpos.R
import com.symplified.easydukanpos.databinding.ActivityMainBinding
import com.symplified.easydukanpos.models.stores.BusinessType
import com.symplified.easydukanpos.ui.login.LoginActivity
import com.symplified.easydukanpos.ui.main.menu_and_cart.PaymentActivity
import com.symplified.easydukanpos.viewmodels.CartViewModel
import com.symplified.easydukanpos.viewmodels.MainViewModel
import com.symplified.easydukanpos.viewmodels.OrderResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


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
        (headerView.findViewById(R.id.app_version_text) as TextView).text =
            getString(
                R.string.version_indicator,
                Calendar.getInstance().get(Calendar.YEAR),
                BuildConfig.VERSION_NAME
            )

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

        else -> super.onOptionsItemSelected(item)
    }

    private fun showSnackBar(message: String) = Snackbar.make(
        binding.root,
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}