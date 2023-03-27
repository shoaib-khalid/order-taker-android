package com.symplified.ordertaker.ui.main.barcode_scanner

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.ActivityBarcodeScannerBinding

class BarcodeScannerActivity : AppCompatActivity(), TorchListener {

    private lateinit var capture: CaptureManager
    private lateinit var binding: ActivityBarcodeScannerBinding
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private var isFlashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barcodeScannerView = binding.zxingBarcodeScanner
        barcodeScannerView.setTorchListener(this)

        capture = CaptureManager(this, barcodeScannerView)
            .apply {
                initializeFromIntent(intent, savedInstanceState)
                decode()
            }

        if (applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            binding.flashButton.setOnClickListener {
//                if (isFlashOn) {
//                    barcodeScannerView.setTorchOff()
//                } else {
//                    barcodeScannerView.setTorchOn()
//                }
                when (isFlashOn) {
                    true -> barcodeScannerView.setTorchOff()
                    false -> barcodeScannerView.setTorchOn()
                }
            }
            binding.flashButton.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        capture.onSaveInstanceState(outState)
    }

    override fun onTorchOn() {
        isFlashOn = true
        binding.flashButton.setImageResource(R.drawable.ic_flashlight_on)
    }

    override fun onTorchOff() {
        isFlashOn = false
        binding.flashButton.setImageResource(R.drawable.ic_flashlight_off)
    }
}