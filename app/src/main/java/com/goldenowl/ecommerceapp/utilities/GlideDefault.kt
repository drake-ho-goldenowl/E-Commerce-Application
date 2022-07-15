package com.goldenowl.ecommerceapp.utilities

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.R

internal object GlideDefault {
    fun show(context: Context, url: String, imageView: ImageView, isSave: Boolean = false) {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.start()

        if (isSave) {
            Glide.with(context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.img_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        } else {
            Glide.with(context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.img_default)
                .into(imageView)
        }
    }

    fun userImage(context: Context, url: String, imageView: ImageView) {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(context)
            .load(url)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_no_login)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
}