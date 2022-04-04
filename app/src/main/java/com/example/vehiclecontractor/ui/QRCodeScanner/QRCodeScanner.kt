package com.example.vehiclecontractor.ui.QRCodeScanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.vehiclecontractor.Extentions.runOnUiThread
import com.example.vehiclecontractor.R
import com.example.vehiclecontractor.databinding.FragmentHomeBinding
import com.example.vehiclecontractor.databinding.FragmentQrCodeScannerBinding

class QRCodeScanner : Fragment() {
    private var _binding: FragmentQrCodeScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQrCodeScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val scannerView = binding.scannerView
        codeScanner = CodeScanner(this.requireContext(), scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.startPreview()
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this.requireContext(), "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                val userRequest = QRCodeScannerDirections.actionQRCodeScannerToSendClientRequestVC(it.text)
                findNavController().navigate(userRequest)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this.requireContext(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

//        scannerView.setOnClickListener {
//            codeScanner.startPreview()
//        }

        return root
    }
}