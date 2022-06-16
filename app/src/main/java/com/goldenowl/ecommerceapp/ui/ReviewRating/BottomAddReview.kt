package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.goldenowl.ecommerceapp.databinding.BottomLayoutAddYourReviewBinding
import com.goldenowl.ecommerceapp.viewmodels.ReviewRatingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomAddReview(private val idProduct: String) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomLayoutAddYourReviewBinding
    private val viewModel: ReviewRatingViewModel by viewModels()
    private var starVote: Long = 0
    private var description: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutAddYourReviewBinding.inflate(inflater, container, false)

        bind()
        setupObserve()
        return binding.root
    }

    fun bind() {
        binding.apply {
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                starVote = rating.toLong()
            }


            edittextDescription.addTextChangedListener(object : TextWatcher {
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
                    description = s.toString()
                }
            })


            btnSendReview.setOnClickListener {
                viewModel.insertReview(
                    "v5Q20ZPTbaPMiY4Eq4YRQh4Opez2", idProduct, description, starVote,
                    emptyList()
                )
            }

        }
    }

    fun setupObserve() {
        viewModel.dismiss.observe(viewLifecycleOwner) {
            if (it) {
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "BOTTOM_ADD_REVIEW"
    }
}