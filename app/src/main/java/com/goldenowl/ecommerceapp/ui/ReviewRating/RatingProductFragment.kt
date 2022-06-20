package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListImageReview
import com.goldenowl.ecommerceapp.adapters.ListReviewAdapter
import com.goldenowl.ecommerceapp.data.RatingProduct
import com.goldenowl.ecommerceapp.databinding.FragmentRatingProductBinding
import com.goldenowl.ecommerceapp.viewmodels.ReviewRatingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatingProductFragment : Fragment() {
    private lateinit var binding: FragmentRatingProductBinding
    private lateinit var idProduct: String
    private lateinit var adapterReview: ListReviewAdapter
    private val viewModel: ReviewRatingViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT).toString()
        }
        binding = FragmentRatingProductBinding.inflate(inflater, container, false)

        if (idProduct.isNotBlank()) {
            viewModel.getDataLive(idProduct)
            viewModel.statusRatingProduct.value = idProduct
        }

        adapterReview = ListReviewAdapter({

        }, { txtName, imgAvatar, userID ->
            val result = viewModel.getNameAndAvatarUser(userID)
            result.observe(viewLifecycleOwner) {
                txtName.text = it.first
                Glide.with(this)
                    .load(it.second)
                    .error(R.drawable.img_sample_2)
                    .into(imgAvatar)
            }
        }, { review, txtHelpful, icLike ->
            if (adapterReview.isHelpful) {
                viewModel.removeHelpful(review)
                adapterReview.isHelpful = false
            } else {
                viewModel.addHelpful(review)
                adapterReview.isHelpful = true
            }
            setColorHelpful(adapterReview.isHelpful, txtHelpful, icLike)
        }, { review, txtHelpful, icLike ->
            viewModel.checkHelpfulForUser(review)
                .observe(viewLifecycleOwner) {
                    adapterReview.isHelpful = it
                    setColorHelpful(it, txtHelpful, icLike)
                }
        }, { recyclerView, review ->

            val adapter = ListImageReview(this@RatingProductFragment, false, {}, {})
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

    private fun setColorHelpful(isHelpful: Boolean, txtHelpful: TextView, icLike: ImageView) {
        if (isHelpful) {
            txtHelpful.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            icLike.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_like2)
            )
        } else {
            txtHelpful.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            icLike.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_like)
            )
        }
    }

    private fun setupObserve() {
        viewModel.listReview.observe(viewLifecycleOwner) {
            adapterReview.submitList(it)
            viewModel.fetchRatingProduct(idProduct)
            binding.txtNumberReview.text = "${it.size} reviews"
        }
        viewModel.ratingProduct.observe(viewLifecycleOwner) {
            if (it != null) {
                setupRatingStatistics(it)
            }
        }
    }

    private fun setupRatingStatistics(ratingProduct: RatingProduct) {
        binding.apply {
            val totalRating = ratingProduct.getTotalRating()
            txtRatingAverage.text = ratingProduct.getAverageRating().toString()
            txtNumberRating.text = "$totalRating ratings"
            progressBar1.progress = ((ratingProduct.rating[0] / totalRating) * 100).toInt()
            progressBar2.progress = ((ratingProduct.rating[1] / totalRating) * 100).toInt()
            progressBar3.progress = ((ratingProduct.rating[2] / totalRating) * 100).toInt()
            progressBar4.progress = ((ratingProduct.rating[3] / totalRating) * 100).toInt()
            progressBar5.progress = ((ratingProduct.rating[4] / totalRating) * 100).toInt()

            txtNumberRating1.text = ratingProduct.rating[0].toString()
            txtNumberRating2.text = ratingProduct.rating[1].toString()
            txtNumberRating3.text = ratingProduct.rating[2].toString()
            txtNumberRating4.text = ratingProduct.rating[3].toString()
            txtNumberRating5.text = ratingProduct.rating[4].toString()
        }
    }

    fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.ratingandreviews)
            btnAddReview.setOnClickListener {
                val bottomAddReview = BottomAddReview(idProduct)
                bottomAddReview.show(parentFragmentManager, BottomAddReview.TAG)
            }
            recyclerVIewReview.layoutManager = LinearLayoutManager(context)
            recyclerVIewReview.adapter = adapterReview

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            checkboxWithPhoto.setOnClickListener {
                viewModel.filterImage(checkboxWithPhoto.isChecked)
            }

            if (!viewModel.userManager.isLogged()) {
                btnAddReview.visibility = View.GONE
            }
        }
    }

    companion object {
        const val ID_PRODUCT = "idProduct"
    }
}