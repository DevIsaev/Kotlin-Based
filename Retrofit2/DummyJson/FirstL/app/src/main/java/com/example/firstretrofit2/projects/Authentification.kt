package com.example.firstretrofit2.projects

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.firstretrofit2.MainActivity.Companion.retrofit
import com.example.firstretrofit2.R
import com.example.firstretrofit2.databinding.FragmentAuthentificationBinding
import com.example.firstretrofit2.retrofit.API
import com.example.firstretrofit2.retrofit.authReuest
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Authentification : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentAuthentificationBinding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAuthentificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Связь с интерфейсом
        val Api = retrofit.create(API::class.java)
        binding.button.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Выполнение сетевого запроса в IO потоке
                    val User = withContext(Dispatchers.IO) {
                        Api.authFun(authReuest(binding.username.text.toString(), binding.password.text.toString()))
                    }


                   Picasso.get().load(User.image).into(binding.imageView)
                    binding.RESULT.text=User.firstName+"\n"+User.lastName



                } catch (e: Exception) {
                    // Обработка ошибок (например, показать тост)
                    e.printStackTrace()
                    binding.RESULT.text=e.toString()
                }
            }
        }

    }
}