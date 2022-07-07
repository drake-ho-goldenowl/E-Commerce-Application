package com.goldenowl.ecommerceapp.ui.QrScan

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentQrScanBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QrScanFragment : Fragment() {
    private val viewModel: QrScanViewModel by viewModels()
    private lateinit var binding: FragmentQrScanBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var requestCamera: ActivityResultLauncher<String>
    private val handlerFragment = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCamera =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
                    startScanning()
                } else {
                    Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrScanBinding.inflate(inflater, container, false)
        setupObserve()
        bind()
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startScanning()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                val action = QrScanFragmentDirections.actionQrScanFragmentToAllowCameraFragment()
                findNavController().navigate(action)
            }
            else -> {
                requestCamera.launch(Manifest.permission.CAMERA)
            }
        }
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            statusCheckProduct.observe(viewLifecycleOwner) {
                if (it.isNotBlank()) {
                    if (it != NULL) {
                        val action =
                            QrScanFragmentDirections.actionQrScanFragmentToProductDetailFragment(
                                idProduct = it
                            )
                        findNavController().navigate(action)
                    } else {
                        incorrectProductPopup()
                    }
                }
            }
        }
    }

    private fun bind() {
        binding.apply {
            codeScanner = CodeScanner(requireContext(), scannerView)
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun startScanning() {
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                handlerFragment.removeMessages(0)
                if (it.text.isNotBlank()) {
                    viewModel.checkProduct(it.text)
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun timeOutDetectedQR() {
        handlerFragment.removeMessages(0)
        handlerFragment.postDelayed({
            popUpTimeOutPopup()
        }, TIME_OUT.toLong())
    }

    private fun popUpTimeOutPopup() {
        pauseCamera()
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.title_time_out))
            .setMessage(getString(R.string.message_time_out))
            .setPositiveButton(getString(R.string.message_continue)) { dialog, _ ->
                resumeCamera()
                timeOutDetectedQR()
                dialog.dismiss()
            }
            .setNegativeButton(
                getString(R.string.close)
            ) { dialog, _ ->
                findNavController().navigateUp()
                dialog.dismiss()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun incorrectProductPopup() {
        pauseCamera()
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.title_incorrect))
            .setMessage(getString(R.string.message_incorrect))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                resumeCamera()
                timeOutDetectedQR()
                dialog.dismiss()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun resumeCamera() {
        if (::codeScanner.isInitialized) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                codeScanner.startPreview()
            }
        }
    }

    private fun pauseCamera() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
    }

    override fun onResume() {
        super.onResume()
        timeOutDetectedQR()
        resumeCamera()
    }

    override fun onPause() {
        super.onPause()
        handlerFragment.removeMessages(0)
        pauseCamera()
    }

    override fun onDestroy() {
        handlerFragment.removeMessages(0)
        codeScanner.isFlashEnabled = false
        super.onDestroy()
    }

    companion object {
        const val TIME_OUT = 1000 * 60 * 2
        const val NULL = "NULL"
    }
}