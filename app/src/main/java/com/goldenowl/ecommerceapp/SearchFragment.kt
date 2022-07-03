package com.goldenowl.ecommerceapp

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListCategoryAdapter
import com.goldenowl.ecommerceapp.adapters.ListHistoryAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentSearchBinding
import com.goldenowl.ecommerceapp.utilities.HISTORY
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: ShopViewModel by viewModels()
    private var listHistory: MutableList<String> = mutableListOf()
    private lateinit var binding: FragmentSearchBinding
    private var listCategory: List<String> = emptyList()
    private lateinit var adapterHistoryAdapter: ListHistoryAdapter
    private lateinit var adapterCategoryAdapter: ListCategoryAdapter
    private var isViewAll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        loadData()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupObserve()
        setupAdapter()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            allCategory.observe(viewLifecycleOwner) {
                listCategory = it
                if (!isViewAll) {
                    adapterCategoryAdapter.submitList(setDefaultList())
                } else {
                    adapterCategoryAdapter.submitList(it)
                }
            }
        }
    }

    private fun setupAdapter() {
        adapterHistoryAdapter = ListHistoryAdapter {
            actionFragment("", it)
        }

        adapterCategoryAdapter = ListCategoryAdapter { str ->
            actionFragment(str, null)
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


            adapterHistoryAdapter.submitList(listHistory.toList())

            recyclerViewCategory.layoutManager =
                GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewCategory.adapter = adapterCategoryAdapter



            btnViewAll.setOnClickListener {
                adapterCategoryAdapter.submitList(listCategory)
                btnViewAll.visibility = View.GONE
                isViewAll = true
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            editTextSearch.requestFocus()
            editTextSearch.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if ((event?.action == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)
                    ) {
                        checkHistory(editTextSearch.text.toString())
                        saveData()
                        actionFragment("", editTextSearch.text.toString())
                        return true
                    }
                    return false
                }
            })
            btnQR.setOnClickListener {
                findNavController().navigate(R.id.qrScanFragment)
            }
        }
    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences? =
            activity?.getSharedPreferences(HISTORY, MODE_PRIVATE)
        sharedPreferences?.let {
            val json = sharedPreferences.getString(HISTORY, "")
            if (!json.isNullOrBlank()) {
                val type = object : TypeToken<List<String?>?>() {}.type
                val arrPackageData = Gson().fromJson<List<String>>(json, type)
                listHistory.addAll(arrPackageData)
            }
        }
    }

    private fun saveData() {
        val sharedPreferences: SharedPreferences? =
            activity?.getSharedPreferences(HISTORY, MODE_PRIVATE)
        val json = Gson().toJson(listHistory)
        sharedPreferences?.let {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(HISTORY, json)
            editor.commit()
        }
    }

    private fun setDefaultList(): List<String> {
        if (listCategory.size >= MAX_CATEGORY) {
            binding.btnViewAll.visibility = View.VISIBLE
            return listCategory.subList(0, MAX_CATEGORY)
        }
        binding.btnViewAll.visibility = View.GONE
        return listCategory
    }

    private fun checkHistory(string: String) {
        if (listHistory.contains(string)) {
            return
        }
        if (listHistory.size > 9) {
            listHistory.removeAt(0)
        }
        listHistory.add(string)


    }

    fun actionFragment(category: String, name: String?) {
        val action = SearchFragmentDirections.actionSearchFragmentToCatalogFragment(
            nameCategories = category,
            nameProduct = name
        )
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.editTextSearch.clearFocus()
    }

    companion object {
        const val MAX_CATEGORY = 4
        const val GRIDVIEW_SPAN_COUNT = 2
    }
}