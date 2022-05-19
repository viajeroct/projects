package com.viajero.androidtutorial.fourth

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class SimpleViewModel : ViewModel() {
    private val _name = MutableLiveData("Nikita")
    private val _lastName = MutableLiveData("Trofimov")
    private val _likes = MutableLiveData(0)

    val name = _name
    val lastName = _lastName
    var likes = _likes
    // TODO: deprecate 2.0 : private set

    lateinit var image: LiveData<Drawable>

    fun bindDrawable(normal: Drawable, popular: Drawable) {
        image = Transformations.map(likes) {
            when {
                it > 4 -> popular
                else -> normal
            }
        }
    }

    fun onLike() {
        _likes.value = (_likes.value ?: 0) + 1
    }

    val popularity: LiveData<Popularity> = Transformations.map(_likes) {
        when {
            it > 9 -> Popularity.STAR
            it > 4 -> Popularity.POPULAR
            else -> Popularity.NORMAL
        }
    }

    /* TODO: deprecated 2.0
    val popularity: Popularity
        get() {
            return when {
                likes > 9 -> Popularity.STAR
                likes > 4 -> Popularity.POPULAR
                else -> Popularity.NORMAL
            }
        }
     */
}

enum class Popularity {
    NORMAL,
    POPULAR,
    STAR
}
