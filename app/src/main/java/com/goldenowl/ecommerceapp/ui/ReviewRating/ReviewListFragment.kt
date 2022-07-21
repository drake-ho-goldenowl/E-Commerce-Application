package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListImageReview
import com.goldenowl.ecommerceapp.adapters.ListReviewAdapter2
import com.goldenowl.ecommerceapp.data.TypeSort
import com.goldenowl.ecommerceapp.databinding.FragmentReviewListBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment

class ReviewListFragment : BaseFragment() {
    private val viewModel: ReviewRatingViewModel by viewModels()
    private lateinit var binding: FragmentReviewListBinding
    private lateinit var adapterReview: ListReviewAdapter2
    private val txtFilter = listOf("Date ASC", "Date DES","Star ASC","Star DES")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllReviewOfUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewListBinding.inflate(inflater, container, false)
        adapterReview = ListReviewAdapter2 { recyclerView, review ->
            val adapter = ListImageReview(false, {}, {})
            adapter.dataSet = review.listImage
            adapter.notifyDataSetChanged()

            recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            recyclerView.adapter = adapter
        }

        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            reviews.observe(viewLifecycleOwner) {
                binding.nestedScrollView.scrollTo(0,0)
                adapterReview.submitList(it)
            }
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.review_list)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerVIewReview.layoutManager = LinearLayoutManager(context)
            recyclerVIewReview.adapter = adapterReview

            appBarLayout.btnFilter.setOnClickListener {
                showMenu(it)
            }
        }
    }

    private fun showMenu(v: View) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(R.menu.menu_filter_review, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.sortDateAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[0]
                    viewModel.filterReview(FilterReview.DATE, TypeSort.ASCENDING)
                    false
                }
                R.id.sortDateDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[1]
                    viewModel.filterReview(FilterReview.DATE,TypeSort.DESCENDING)
                    false
                }
                R.id.sortStarAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[2]
                    viewModel.filterReview(FilterReview.STAR,TypeSort.ASCENDING)
                    false
                }
                R.id.sortStarDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[3]
                    viewModel.filterReview(FilterReview.STAR,TypeSort.DESCENDING)
                    false
                }
                else ->false
            }
        }

        popup.setOnDismissListener {
        }
        popup.show()
    }
}