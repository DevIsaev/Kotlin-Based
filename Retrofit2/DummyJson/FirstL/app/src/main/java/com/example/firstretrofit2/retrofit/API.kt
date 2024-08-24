package com.example.firstretrofit2.retrofit

import com.example.firstretrofit2.projects.Products
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//выполнения запросов
interface API {
    //получение 1 продукта
    @GET("/products/{id}")
    //функция, обращается к dataclass
    suspend fun getProductByID(@Path("id") id: Int):Product

    //авторизация через post
    @POST("auth/login")
    suspend fun authFun(@Body authRequest:authReuest):User


    //получение всех продуктов
    @GET("/products")
    //функция, обращается к dataclass
    //не указывать List<Product> тк ожидается объект
    suspend fun getAllProducts():Products

    //получения списка продуктов по поиску
//    @GET("/products/search")
//    //функция, обращается к dataclass
//    suspend fun getProductBySearch(@Query("q") name: String):Products

    //авторизация с токеном, для его запроса в каждую предыдущую ссылку добавить auth/
    //хэдеры
    @Headers("'Content-Type: ''application/json'")
    @GET("auth/products/search")
    //функция, обращается к dataclass, указывается хэдер для передачи токена
    suspend fun getProductBySearchByAuth(@Header("Authorization") token: String, @Query("q") name: String):Products

    //авторизация с токеном, для его запроса в каждую предыдущую ссылку добавить auth/
    //хэдеры
    @GET("auth/products/search")
    //функция, обращается к dataclass, указывается хэдер для передачи токена
    suspend fun getProductBySearch(@Query("q") name: String):Products
}