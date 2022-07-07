package com.goldenowl.ecommerceapp.ui.QrScan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.databinding.FragmentAllowCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllowCameraFragment : Fragment() {
    private lateinit var binding: FragmentAllowCameraBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllowCameraBinding.inflate(inflater, container, false)
        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            btnClose.setOnClickListener {
                findNavController().navigateUp()
            }
            btnNotNow.setOnClickListener {
                findNavController().navigateUp()
            }

            btnGoToSetting.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireActivity().packageName)
                findNavController().navigateUp()
                startActivity(intent)
            }
        }
    }
}