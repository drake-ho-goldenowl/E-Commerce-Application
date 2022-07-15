package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListImageReview
import com.goldenowl.ecommerceapp.adapters.ListReviewAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentRatingProductBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.utilities.GlideDefault
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class RatingProductFragment : BaseFragment() {
    private lateinit var binding: FragmentRatingProductBinding
    private lateinit var idProduct: String
    private lateinit var adapterReview: ListReviewAdapter
    private val viewModel: ReviewRatingViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT).toString()
            if (idProduct.isNotBlank()) {
                viewModel.isLoading.postValue(true)
                viewModel.setIdProduct(idProduct)
            } else {
                findNavController().popBackStack(R.id.productDetailFragment, false)
            }
        }
        binding = FragmentRatingProductBinding.inflate(inflater, container, false)

        adapterReview = ListReviewAdapter({

        }, { txtName, imgAvatar, userID ->
            val result = viewModel.getNameAndAvatarUser(userID)
            result.observe(viewLifecycleOwner) {
                txtName.text = it.first
                GlideDefault.show(requireContext(), it.second, imgAvatar,false)
            }
        }, { review, txtHelpful, icLike, isHelpful ->
            if (isHelpful) {
                viewModel.addHelpful(review)

            } else {
                viewModel.removeHelpful(review)
            }
            setColorHelpful(isHelpful, txtHelpful, icLike)

        }, { review, txtHelpful, icLike ->
            viewModel.checkHelpfulForUser(review)
                .observe(viewLifecycleOwner) {
                    adapterReview.isHelpful = it
                    setColorHelpful(it, txtHelpful, icLike)
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
        viewModel.apply {
            listReview.observe(viewLifecycleOwner) {
                filterImage(statusFilterImage.value ?: false)?.let { list ->
                    adapterReview.submitList(list)
                    binding.txtNumberReview.text = "${list.size} reviews"
                    isLoading.postValue(false)
                }
            }

            product.observe(viewLifecycleOwner) { product ->
                if (product != null) {
                    getReview(product.id)
                    getRatingProduct(product.id)
                }
            }

            listRating.observe(viewLifecycleOwner) {
                product.value?.let { list ->
                    setupRatingStatistics(list, it)
                }
            }

            statusFilterImage.observe(viewLifecycleOwner) {
                filterImage(it)?.let { list ->
                    adapterReview.submitList(list)
                    binding.txtNumberReview.text = "${list.size} reviews"
                }
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }

    }

    private fun setupRatingStatistics(product: Product, ratingProduct: List<Int>) {
        binding.apply {
            val totalRating = product.numberReviews.toFloat()
            txtRatingAverage.text = product.reviewStars.toString()
            txtNumberRating.text = "${totalRating.roundToInt()} ratings"
            progressBar1.progress = ((ratingProduct[0] / totalRating) * 100).toInt()
            progressBar2.progress = ((ratingProduct[1] / totalRating) * 100).toInt()
            progressBar3.progress = ((ratingProduct[2] / totalRating) * 100).toInt()
            progressBar4.progress = ((ratingProduct[3] / totalRating) * 100).toInt()
            progressBar5.progress = ((ratingProduct[4] / totalRating) * 100).toInt()

            txtNumberRating1.text = ratingProduct[0].toString()
            txtNumberRating2.text = ratingProduct[1].toString()
            txtNumberRating3.text = ratingProduct[2].toString()
            txtNumberRating4.text = ratingProduct[3].toString()
            txtNumberRating5.text = ratingProduct[4].toString()
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
                viewModel.statusFilterImage.postValue(checkboxWithPhoto.isChecked)
            }

            if (!viewModel.isLogged()) {
                btnAddReview.visibility = View.GONE
            }
        }
    }

}