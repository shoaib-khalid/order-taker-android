package com.symplified.ordertaker.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.databinding.ActivityMainBinding
import com.symplified.ordertaker.ui.login.LoginActivity
import com.symplified.ordertaker.viewmodels.AuthViewModel
import com.symplified.ordertaker.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (!isAuthenticated) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
                    as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_logout), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)
        val navHeaderTitle: TextView = headerView.findViewById(R.id.nav_header_title)
        val navHeaderSubtitle: TextView = headerView.findViewById(R.id.nav_header_subtitle)

        mainViewModel.storeName.observe(this) { storeName ->
            if (storeName != null && storeName.isNotBlank()) {
                navHeaderTitle.text = storeName
            } else {
                mainViewModel.fetchStoreName()
            }
        }

        mainViewModel.username.observe(this) { username ->

            if (username != null && username.isNotBlank()) {
                navHeaderSubtitle.text = username
            } else {
                mainViewModel.fetchUsername()
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
}