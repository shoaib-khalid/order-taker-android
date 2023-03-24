package com.symplified.ordertaker.ui.main.menuandcart

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.FragmentBarcodeScannerBinding
import com.symplified.ordertaker.viewmodels.MenuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarcodeScannerFragment : Fragment(), BarcodeListener {

    private var _binding : FragmentBarcodeScannerBinding? = null
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBarcodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            menuViewModel.scannedBarcode.observe(viewLifecycleOwner) { barcode ->
                barcode?.let {
                    Snackbar.make(binding.root, "Barcode scanned: $it", Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
            }
        }

        val barcodeDetector: BarcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        val cameraSource: CameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()

        binding.cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ContextCompat.checkSelfPermission(
                        requireContext().applicationContext,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraSource.start(binding.cameraView.holder)
                } else {
                    findNavController().popBackStack()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(BarCodeDetector(this))
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.isNotEmpty()) {
                    Log.d("hateniggers", "Detector: Barcode scanned: ${barcodes.valueAt(0).displayValue}")
                    menuViewModel.setScannedBarcode(barcodes.valueAt(0).displayValue)
                }
            }
        })
    }

    class BarCodeDetector(private val barcodeListener: BarcodeListener) : Detector.Processor<Barcode> {
        override fun release() {
        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            val barcodes = detections.detectedItems
            if (barcodes.isNotEmpty()) {
                barcodeListener.onBarcodeScanned(barcodes.valueAt(0).displayValue)
            }
        }
    }

    override fun onBarcodeScanned(barcode: String) {
    }
}

interface BarcodeListener {
    fun onBarcodeScanned(barcode: String)
}