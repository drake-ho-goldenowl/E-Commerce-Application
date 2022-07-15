package com.goldenowl.ecommerceapp.ui.ShippingAddress

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCountryAdapter
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectCountryBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.BUNDLE_KEY_NAME_COUNTRY
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.REQUEST_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSelectCountry : BottomSheetDialogFragment() {
    private lateinit var binding: BottomLayoutSelectCountryBinding
    private lateinit var adapter: ListCountryAdapter
    private lateinit var countries: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countries =
            listOf(*resources.getStringArray(R.array.countries_array))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectCountryBinding.inflate(inflater, container, false)

        adapter = ListCountryAdapter {
            sendData(it)
            dismiss()
        }
        adapter.submitList(countries)

        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            recyclerViewCountry.adapter = adapter
            recyclerViewCountry.layoutManager = LinearLayoutManager(context)

            editTextCountry.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.submitList(countries.filter { it.lowercase().contains(s.toString()) })
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    private fun sendData(select: String) {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(BUNDLE_KEY_NAME_COUNTRY to select)
        )
    }

    companion object {
        const val TAG = "BOTTOM_SELECT_COUNTRY"
    }

}