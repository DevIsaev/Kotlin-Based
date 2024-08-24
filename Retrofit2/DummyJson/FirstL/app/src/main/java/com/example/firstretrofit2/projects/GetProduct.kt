package com.example.firstretrofit2.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.firstretrofit2.MainActivity.Companion.retrofit
import com.example.firstretrofit2.databinding.FragmentGetProductBinding
import com.example.firstretrofit2.retrofit.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GetProduct : Fragment() {
    lateinit var binding: FragmentGetProductBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGetProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Связь с интерфейсом
        val productApi = retrofit.create(API::class.java)

        // Использование корутин для выполнения сетевого запроса
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Выполнение сетевого запроса в IO потоке
                val product = withContext(Dispatchers.IO) {
                    productApi.getProductByID(5)
                }

                // Обновление UI на главном потоке
                binding.result.text = "${product.title}\n${product.brand}\n${product.price}\n${product.images[0].toString()}"

            } catch (e: Exception) {
                // Обработка ошибок (например, показать тост)
                e.printStackTrace()
                // Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
