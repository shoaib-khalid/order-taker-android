package com.symplified.ordertaker.ui.main.menu_and_cart

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.databinding.FragmentBarcodeScannerBinding
import com.symplified.ordertaker.viewmodels.MenuViewModel
import kotlinx.coroutines.launch

class BarcodeScannerFragment : Fragment() {

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

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.isNotEmpty()) {
                    menuViewModel.setScannedBarcode(barcodes.valueAt(0).displayValue)
                }
            }
        })
    }
}