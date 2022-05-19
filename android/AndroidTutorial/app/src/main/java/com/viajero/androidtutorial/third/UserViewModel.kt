package com.viajero.androidtutorial.third

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var userList: MutableLiveData<List<User>> = MutableLiveData()

    init {
        userList.value = UserData.getUsers()
    }

    fun getListUsers() = userList

    fun updateListUsers() {
        userList.value = UserData.getAnotherUsers()
    }
}
