package com.goldenowl.ecommerceapp.utilities

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.R

internal object GlideDefault {
    fun show(context: Context, url: String, imageView: ImageView, isSave: Boolean = false) {
        val circularProgressDrawable = createCircularProgress(context)

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

    fun showHome(context: Context, url: String, imageView: ImageView) {
        val circularProgressDrawable = createCircularProgress(context)

        Glide.with(context)
            .load(url)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.img_home)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    fun userImage(context: Context, url: String, imageView: ImageView) {
        val circularProgressDrawable = createCircularProgress(context)

        Glide.with(context)
            .load(url)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_no_login)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    private fun createCircularProgress(context: Context): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }
}