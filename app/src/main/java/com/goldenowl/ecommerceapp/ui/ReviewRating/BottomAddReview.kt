package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListImageReview
import com.goldenowl.ecommerceapp.databinding.BottomLayoutAddYourReviewBinding
import com.goldenowl.ecommerceapp.ui.BaseBottomSheetDialog
import com.goldenowl.ecommerceapp.utilities.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BottomAddReview(private val idProduct: String) : BaseBottomSheetDialog() {
    private lateinit var binding: BottomLayoutAddYourReviewBinding
    private val viewModel: ReviewRatingViewModel by activityViewModels()
    private var starVote: Long = 0
    private var description: String = ""
    private val listImage: MutableSet<String> = mutableSetOf()
    private lateinit var adapter: ListImageReview

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutAddYourReviewBinding.inflate(inflater, container, false)

        adapter = ListImageReview(true, {

        }, {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, TITLE_PICK_IMAGE),
                PICK_IMAGE_REQUEST
            )
        })

        adapter.dataSet = listImage.toList()
        adapter.notifyDataSetChanged()
        bind()
        setupObserve()
        return binding.root
    }

    fun bind() {
        binding.apply {
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                starVote = rating.toLong()
                if (starVote in 1..5) {
                    viewModel.alertStar.postValue(false)
                }
            }

            recyclerViewImageReview.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )

            recyclerViewImageReview.adapter = adapter

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
                    if (description.isNotBlank()) {
                        viewModel.alertDescription.postValue(false)
                    }
                }
            })

            btnSendReview.setOnClickListener {
                val review =
                    viewModel.createReview(idProduct, description, starVote, listImage.toList())
                review?.let {
                    viewModel.isLoading.postValue(true)
                    if (listImage.isNotEmpty()) {
                        val result = viewModel.uploadImage(
                            review.createdTimer?.seconds.toString(),
                            listImage.toList()
                        )
                        result.first.observe(viewLifecycleOwner) {
                            review.listImage = it
                            if (review.listImage.size == listImage.size) {
                                viewModel.insertReview(review)
                            }
                        }
                        result.second.observe(viewLifecycleOwner) {
                            if (!it) {
                                viewModel.toastMessage.postValue(FAILURE)
                                viewModel.dismiss.postValue(true)
                                viewModel.isLoading.postValue(false)
                            }
                        }
                    } else {
                        viewModel.insertReview(review)
                    }
                }
            }
        }
    }

    private fun setupObserve() {
        viewModel.apply {
            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    dismiss()
                }
            }
            alertStar.observe(viewLifecycleOwner) {
                if (it) {
                    binding.txtAlertStar.visibility = View.VISIBLE
                } else {
                    binding.txtAlertStar.visibility = View.GONE
                }
            }

            alertDescription.observe(viewLifecycleOwner) {
                if (it) {
                    binding.txtAlertDescription.visibility = View.VISIBLE
                } else {
                    binding.txtAlertDescription.visibility = View.GONE
                }
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)
                toastMessage.postValue("")
            }

            isLoading.observe(viewLifecycleOwner){
                setLoading(it)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            val filePath = data.data
            filePath?.let {
                FileUtil.from(requireContext(), it).let { file ->
                    lifecycleScope.launch {
                        Compressor.compress(requireContext(), file) {
                            quality(QUALITY)
                            format(Bitmap.CompressFormat.JPEG)
                            size(MAXSIZE.toLong()) // 1 MB
                        }.let { newFile ->
                            listImage.add(newFile.toString())
                            adapter.dataSet = listImage.toList()
                            adapter.notifyDataSetChanged()
                        }

                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "BOTTOM_ADD_REVIEW"
        const val TITLE_PICK_IMAGE = "Select Picture"
        const val PICK_IMAGE_REQUEST = 100
        const val SUCCESS = "Upload success"
        const val FAILURE = "Upload failure"
        const val QUALITY = 80
        const val MAXSIZE = 1_000_000
    }
}