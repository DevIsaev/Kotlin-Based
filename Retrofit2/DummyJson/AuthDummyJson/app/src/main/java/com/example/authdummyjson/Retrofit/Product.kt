package com.example.firstretrofit2.retrofit

data class Product(var id:Int, var title:String,
                   var decription:String,
                   var price:Float, var discount:Float, var rating:Float,
                   var stock:Int, var brand:String, var Category:String,
                   var thumbnail:String, var images:List<String>)
