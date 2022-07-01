package com.goldenowl.ecommerceapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListCategoryAdapter
import com.goldenowl.ecommerceapp.adapters.ListHistoryAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentSearchBinding
import com.goldenowl.ecommerceapp.utilities.HISTORY
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var listCategory: List<String>
    private lateinit var listProduct: List<Product>
    private val listHistory: MutableSet<String> = mutableSetOf()
    private val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
    private lateinit var adapterHistoryAdapter: ListHistoryAdapter
    private lateinit var adapterCategoryAdapter: ListCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        bind()
        setupAdapter()
        return binding.root
    }

    private fun setupAdapter() {
        adapterHistoryAdapter = ListHistoryAdapter {
            binding.editTextSearch.setText(it)
        }

        adapterCategoryAdapter = ListCategoryAdapter { str ->

        }
    }

    private fun bind() {
        binding.apply {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager.alignItems = AlignItems.FLEX_START
            recyclerViewHistory.layoutManager = layoutManager

            recyclerViewHistory.adapter = adapterHistoryAdapter

            var list = sharedPref?.getStringSet(HISTORY, null)
            if (list == null) {
                list = emptySet()
            }
            println(list)
            listHistory.addAll(list)

            adapterHistoryAdapter.submitList(listHistory.toList())

            recyclerViewCategory.layoutManager =
                GridLayoutManager(context,GRIDVIEW_SPAN_COUNT)
            recyclerViewCategory.adapter = adapterCategoryAdapter

            btnViewAll.setOnClickListener {
                adapterCategoryAdapter.submitList(listCategory)
                btnViewAll.visibility = View.GONE
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                    } else {
                    }
                }

            })

            editTextSearch.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (editTextSearch.text.isNotBlank()) {
                        listHistory.add(editTextSearch.text.toString())
                        sharedPref?.edit()?.putStringSet(HISTORY, listHistory)?.apply()
                    }
                }
            }
        }
    }

    companion object {
        const val MAX_CATEGORY = 4
        const val GRIDVIEW_SPAN_COUNT = 2
    }
}