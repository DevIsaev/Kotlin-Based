package com.example.authdummyjson.Retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginView: ViewModel() {
    var token=MutableLiveData<String>()
}