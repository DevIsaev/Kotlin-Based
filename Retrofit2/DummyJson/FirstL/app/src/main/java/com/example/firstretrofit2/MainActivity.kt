package com.example.firstretrofit2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.firstretrofit2.databinding.ActivityMainBinding
import com.example.firstretrofit2.projects.Authentification
import com.example.firstretrofit2.projects.GetProduct
import com.example.firstretrofit2.projects.ListAllElements
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var binding: ActivityMainBinding
        var bottomNavigation: CurvedBottomNavigation?=null
        var viewPager: ViewPager2? =null

        //интерептор, для проверки получаемых данных
        var interseptor= HttpLoggingInterceptor()
        //клиент
        var client:OkHttpClient=OkHttpClient.Builder().addInterceptor(interseptor).build()
        //инстанция retrofit
        var retrofit=Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")//базовая ссылка api
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()//конвертер в gson формат



    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BottomNavigation()

        interseptor.level=HttpLoggingInterceptor.Level.BODY
    }
    //BottomNavigation
    fun BottomNavigation() {

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation?.add(CurvedBottomNavigation.Model(1, "", R.drawable.baseline_api_24))
        bottomNavigation?.add(CurvedBottomNavigation.Model(2, "", R.drawable.baseline_api_24))
        bottomNavigation?.add(CurvedBottomNavigation.Model(3, "", R.drawable.baseline_api_24))

        viewPager =findViewById(R.id.viewPager)
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(GetProduct())
        adapter.addFragment(Authentification())
        adapter.addFragment(ListAllElements())
        viewPager?.adapter = adapter


        try {
            bottomNavigation?.setOnClickMenuListener {
                try {
                    viewPager?.setCurrentItem(it.id - 1, true)
                }catch (ex:Exception){binding.toolbar.setTitle(ex.toString())}
            }
        }
        catch (ex:Exception){
            Toast.makeText(this,ex.toString(), Toast.LENGTH_LONG).show()
        }

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigation?.show(position + 1)
                invalidateOptionsMenu()
                when (position) {
                    0 -> {
                        binding.toolbar.setTitle("Получить товар")
                    }
                    1 -> {
                        binding.toolbar.setTitle("Авторизация")
                    }
                    2 -> {
                        binding.toolbar.setTitle("Получение списка")
                    }
                    3 -> {
                        binding.toolbar.setTitle("Настройки")
                    }
                    4 -> {
                        binding.toolbar.setTitle("Профиль")
                    }
                }
            }
        })
    }
}