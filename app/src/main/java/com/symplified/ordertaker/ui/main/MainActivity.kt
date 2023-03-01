package com.symplified.ordertaker.ui.main

import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.ActivityMainBinding
import com.symplified.ordertaker.ui.login.LoginActivity
import com.symplified.ordertaker.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var btReceiver: BroadcastReceiver? = null

    private val mainViewModel: MainViewModel by viewModels()
    private val printers: MutableSet<BluetoothDevice> = mutableSetOf()
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            permissions.forEach {
//                it.value
//            }
//
//            if (isGranted) {
//                checkPairedBtDevices()
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        if (savedInstanceState == null) {
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
            val navHeaderImage: ImageView = headerView.findViewById(R.id.imageView)

            mainViewModel.user.observe(this) { user ->
                if (user != null) {
                    navHeaderTitle.text = user.storeName
                    navHeaderSubtitle.text = user.name
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }

            mainViewModel.headerImageUrl.observe(this) { assetUrl ->
                Glide.with(this).load(assetUrl).into(navHeaderImage)
            }
        }

        checkPairedBtDevices()

        btReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        Log.d("blt", "BluetoothDevice.ACTION_FOUND")

                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        Log.d("blt", "device name: ${device?.name}")
                        if (ContextCompat.checkSelfPermission(
                                App.applicationContext(),
                                BLUETOOTH
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
//                            if (device?.name?.startsWith("cloudPrint") == true)
                                device?.apply {
                                    if (bondState == BluetoothDevice.BOND_NONE) {
                                        createBond()
                                    }
                                    if (bondState == BluetoothDevice.BOND_BONDED) {
                                        printers.add(this)
                                    }
                                }
                        }
                    }
                }
            }
        }

        registerReceiver(btReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
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
        R.id.action_logout -> {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.action_logout_confirmation))
                .setPositiveButton(getString(R.string.yes)) { _, _ -> mainViewModel.logout() }
                .setNegativeButton(getString(R.string.no), null)
                .show()
            true
        }
        R.id.action_test_print -> {
            if (ContextCompat.checkSelfPermission(
                    App.applicationContext(),
                    BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (printers.isEmpty()) {
                    Toast.makeText(this, "No printers paired", Toast.LENGTH_SHORT).show()
                }

                printers.forEach { btPrinter ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val socket =
                            btPrinter.createRfcommSocketToServiceRecord(btPrinter.uuids[0].uuid)
                        try {
                            socket.connect()
                            withContext(Dispatchers.IO) {
                                socket.outputStream.write("\nPrint Test\nLine 1\nLine 2\nLine 3\n".toByteArray())
                            }
                            Log.d("blt", "Connected to ${btPrinter.name}: ${socket.isConnected}")
                        } catch (_: Throwable) {
                        }
                    }
                }
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        btReceiver?.let {
            unregisterReceiver(btReceiver)
        }
    }

    private fun checkPairedBtDevices() {
        if (
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(App.applicationContext(), BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
            || (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(App.applicationContext(), BLUETOOTH) == PackageManager.PERMISSION_GRANTED)
        ) {
            val mBluetoothAdapter =
                (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter.bondedDevices

            pairedDevices?.forEach { btDevice ->
                btDevice.fetchUuidsWithSdp()
//                btDevice.uuids.forEach {
//                    Log.d("blt", it.uuid.toString())
//                }
//                Log.d(
//                    "blt",
//                    if (btDevice.uuids.size > 0)
//                        "uuids exist"
//                    else
//                        "uuids empty"
//                )
//                if (btDevice.name.startsWith("CloudPrint")) {
                printers.add(btDevice)
//                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, arrayOf(BLUETOOTH_CONNECT), 2)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(BLUETOOTH), 2)
            }
        }
    }
}