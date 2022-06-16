package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListReviewAdapter
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
        }, {
            viewModel.updateHelpful(it, "v5Q20ZPTbaPMiY4Eq4YRQh4Opez2")
        }, { review, txtHelpful, icLike ->
            viewModel.checkHelpfulForUser(review, "v5Q20ZPTbaPMiY4Eq4YRQh4Opez2")
                .observe(viewLifecycleOwner) {
                    if (it) {
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
        })

        viewModel.listReview.observe(viewLifecycleOwner) {
            adapterReview.submitList(it)
        }
        bind()
        return binding.root
    }

    fun bind() {
        binding.apply {
            btnAddReview.setOnClickListener {
                val bottomAddReview = BottomAddReview(idProduct)
                bottomAddReview.show(parentFragmentManager, BottomAddReview.TAG)
            }
            recyclerVIewReview.layoutManager = LinearLayoutManager(context)
            recyclerVIewReview.adapter = adapterReview

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        const val ID_PRODUCT = "idProduct"
    }
}