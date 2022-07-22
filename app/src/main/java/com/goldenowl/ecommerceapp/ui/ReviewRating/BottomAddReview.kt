package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListImageReview
import com.goldenowl.ecommerceapp.databinding.BottomLayoutAddYourReviewBinding
import com.goldenowl.ecommerceapp.ui.BaseBottomSheetDialog
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.BUNDLE_DISMISS
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.REQUEST_KEY
import com.goldenowl.ecommerceapp.ui.General.DialogChooseImage
import com.goldenowl.ecommerceapp.ui.General.Permission
import com.goldenowl.ecommerceapp.utilities.FileUtil
import com.goldenowl.ecommerceapp.utilities.REQUEST_CAMERA
import com.goldenowl.ecommerceapp.utilities.REQUEST_PICK_IMAGE
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BottomAddReview(private val idProduct: String) : BaseBottomSheetDialog() {
    private lateinit var binding: BottomLayoutAddYourReviewBinding
    private val viewModel: ReviewRatingViewModel by viewModels()
    private var starVote: Long = 0
    private var description: String = ""
    private val listImage: MutableSet<String> = mutableSetOf()
    private lateinit var adapter: ListImageReview
    private lateinit var requestStore: Permission
    private lateinit var requestCamera: Permission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestStore = Permission(this, {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
        }, {})

        requestCamera = Permission(this, {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        }, {})
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutAddYourReviewBinding.inflate(inflater, container, false)

        adapter = ListImageReview(true, {

        }, {
            DialogChooseImage(this, requestCamera, requestStore).show()
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
                    sendData()
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

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_IMAGE -> {
                    selectCompressor(data)
                }
                REQUEST_CAMERA -> {
                    selectCompressor(data, true)
                }
            }
        }
    }

    private fun selectCompressor(data: Intent?, isCamera: Boolean = false) {
        if (data == null) {
            return
        }
        if (isCamera) {
            data.extras?.let { it ->
                FileUtil.getImageUri(
                    requireContext(),
                    it.get(DATA) as Bitmap
                )?.let { uri ->
                    compressorAndUpdateAdapter(uri)
                }
            }
        } else {
            val filePath = data.data
            filePath?.let {
                compressorAndUpdateAdapter(filePath)
            }
        }
    }

    private fun compressorAndUpdateAdapter(filePath: Uri) {
        FileUtil.from(requireContext(), filePath).let { file ->
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

    private fun sendData() {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(BUNDLE_DISMISS to true)
        )
    }


    companion object {
        const val TAG = "BOTTOM_ADD_REVIEW"
        const val SUCCESS = "Upload success"
        const val FAILURE = "Upload failure"
        const val DATA = "data"
        const val QUALITY = 80
        const val MAXSIZE = 1_000_000
    }
}