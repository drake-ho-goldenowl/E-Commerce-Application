package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListImageReview
import com.goldenowl.ecommerceapp.adapters.ListReviewAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentReviewListBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.utilities.GlideDefault

class ReviewListFragment : BaseFragment() {
    private val viewModel: ReviewRatingViewModel by viewModels()
    private lateinit var binding: FragmentReviewListBinding
    private lateinit var adapterReview: ListReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllReviewOfUser()
        viewModel.isLoading.postValue(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewListBinding.inflate(inflater, container, false)
        adapterReview = ListReviewAdapter({

        }, { txtName, imgAvatar, userID ->
            val result = viewModel.getNameAndAvatarUser(userID)
            result.observe(viewLifecycleOwner) {
                txtName.text = it.first
                GlideDefault.show(requireContext(), it.second, imgAvatar, false)
            }
        }, { review, txtHelpful, icLike, isHelpful ->
            if (isHelpful) {
                viewModel.addHelpful(review)

            } else {
                viewModel.removeHelpful(review)
            }
            viewModel.setColorHelpful(requireContext(), isHelpful, txtHelpful, icLike)

        }, { review, txtHelpful, icLike ->
            viewModel.checkHelpfulForUser(review)
                .observe(viewLifecycleOwner) {
                    adapterReview.isHelpful = it
                    viewModel.setColorHelpful(requireContext(), it, txtHelpful, icLike)
                }
        }, { recyclerView, review ->

            val adapter = ListImageReview(false, {}, {})
            adapter.dataSet = review.listImage
            adapter.notifyDataSetChanged()

            recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            recyclerView.adapter = adapter
        })

        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            reviews.observe(viewLifecycleOwner) {
                adapterReview.submitList(it)
                isLoading.postValue(false)
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = "Review List"
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerVIewReview.layoutManager = LinearLayoutManager(context)
            recyclerVIewReview.adapter = adapterReview
        }
    }
}