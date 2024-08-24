package com.example.authdummyjson.Retrofit

import com.example.firstretrofit2.projects.Products
import com.example.firstretrofit2.retrofit.User
import com.example.firstretrofit2.retrofit.authReuest
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface API {
    @POST("auth/login")
    suspend fun auth(@Body authRequest:authReuest):retrofit2.Response<User>

    @Headers("Content-Type: application/json")
    @GET("auth/products/search")
    suspend fun getProductsByNameAuth(@Header("Authorization") token:String, @Query("q") name:String):Products

    @Headers("Content-Type: application/json")
    @GET("auth/products/search")
    suspend fun getAllProducts(@Header("Authorization") token:String):Products
}