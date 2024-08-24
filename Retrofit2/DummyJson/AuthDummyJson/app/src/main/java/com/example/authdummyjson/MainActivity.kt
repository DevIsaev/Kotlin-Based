package com.example.authdummyjson

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.authdummyjson.Retrofit.API
import com.example.authdummyjson.databinding.ActivityMainBinding
import com.example.authdummyjson.databinding.ContentMainBinding
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
companion object{
    //интерептор, для проверки получаемых данных
    var interseptor= HttpLoggingInterceptor()
    //клиент
    var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interseptor).build()
    //инстанция retrofit
    var retrofit=Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")//базовая ссылка api
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()).build()//конвертер в gson формат
    var Api = retrofit.create(API::class.java)
}

    private lateinit var binding: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        interseptor.level=HttpLoggingInterceptor.Level.BODY
    }
}