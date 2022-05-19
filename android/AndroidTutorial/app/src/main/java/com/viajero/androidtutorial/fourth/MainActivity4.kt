package com.viajero.androidtutorial.fourth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.viajero.androidtutorial.R
import com.viajero.androidtutorial.databinding.ActivityMain4Binding

class MainActivity4 : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(SimpleViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: change : setContentView(R.layout.activity_main4)
        val binding: ActivityMain4Binding =
            DataBindingUtil.setContentView(this, R.layout.activity_main4)

        /* TODO: deprecated 2.0
        binding.name = "Nikita"
        binding.lastName = "Trofimov"
        */
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.viewmodel!!.bindDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_person_black_96dp
            )!!,
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_whatshot_black_96dp
            )!!
        )

        // TODO: deprecate 1.0 : updateName()
        // TODO: deprecate 3.0 : updateLikes()
    }

    /* TODO: deprecated 2.0
    fun onLike(view: View) {
        viewModel.onLike()
        updateLikes()
    }
    */

    /* TODO: deprecated 1.0
    private fun updateName() {
        findViewById<TextView>(R.id.plain_name).text = viewModel.name
        findViewById<TextView>(R.id.plain_lastname).text = viewModel.lastName
    }
     */

    /* TODO: deprecated 3.0
    private fun updateLikes() {
        findViewById<TextView>(R.id.likes).text = viewModel.likes.toString()
        findViewById<ProgressBar>(R.id.progressBar).progress =
            (viewModel.likes * 100 / 5).coerceAtMost(100)

        val image = findViewById<ImageView>(R.id.imageView)
        val color = getAssociatedColor(viewModel.popularity, this)

        ImageViewCompat.setImageTintList(image, ColorStateList.valueOf(color))
        image.setImageDrawable(getDrawablePopularity(viewModel.popularity, this))
    }

    private fun getAssociatedColor(popularity: Popularity, context: Context): Int {
        return when (popularity) {
            Popularity.NORMAL -> context.theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.colorForeground)
            ).getColor(0, 0x000000)
            Popularity.POPULAR -> ContextCompat.getColor(context, R.color.popular)
            Popularity.STAR -> ContextCompat.getColor(context, R.color.star)
        }
    }

    private fun getDrawablePopularity(popularity: Popularity, context: Context): Drawable? {
        return when (popularity) {
            Popularity.NORMAL -> ContextCompat.getDrawable(context, R.drawable.ic_person_black_96dp)
            Popularity.POPULAR -> ContextCompat.getDrawable(
                context,
                R.drawable.ic_whatshot_black_96dp
            )
            Popularity.STAR -> ContextCompat.getDrawable(context, R.drawable.ic_whatshot_black_96dp)
        }
    }
    */
}
