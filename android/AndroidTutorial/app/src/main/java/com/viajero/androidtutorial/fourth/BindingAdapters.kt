package com.viajero.androidtutorial.fourth

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import com.viajero.androidtutorial.R

@BindingAdapter("app:hideIfZero")
fun hideIfZero(view: View, number: Int) {
    view.visibility = if (number == 0) View.GONE else View.VISIBLE
}

@BindingAdapter(value = ["app:progressScaled", "android:max"], requireAll = true)
fun setProgress(progressBar: ProgressBar, likes: Int, max: Int) {
    when (likes) {
        in 5..9 -> progressBar.progress = ((likes - 5) * max / 5).coerceAtMost(max)
        in 0..4 -> progressBar.progress = (likes * max / 5).coerceAtMost(max)
        else -> progressBar.progress = ((likes - 10) * max / 50).coerceAtMost(max)
    }
}

@BindingAdapter("app:levelOfPopularity", "android:normal", "android:popular", "android:star")
fun setColorToProgressBar(
    progressBar: ProgressBar,
    likes: Popularity,
    normal: Int,
    popular: Int,
    star: Int
) {
    when (likes) {
        Popularity.NORMAL -> progressBar.progressTintList = ColorStateList.valueOf(normal)
        Popularity.POPULAR -> progressBar.progressTintList = ColorStateList.valueOf(popular)
        Popularity.STAR -> progressBar.progressTintList = ColorStateList.valueOf(star)
    }
}

@SuppressLint("ResourceAsColor")
@BindingAdapter("app:imageForUser", "android:likes")
fun setImageForUser(image: ImageView, setImage: Drawable, likes: Int) {
    if (likes > 9) {
        ImageViewCompat.setImageTintList(image, ColorStateList.valueOf(R.color.white))
    } else if (likes > 4) {
        ImageViewCompat.setImageTintList(image, ColorStateList.valueOf(R.color.black))
    }
    image.setImageDrawable(setImage)
}
