package com.example.firstretrofit2.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstretrofit2.MainActivity
import com.example.firstretrofit2.MainActivity.Companion.retrofit
import com.example.firstretrofit2.R
import com.example.firstretrofit2.adapter.ProductsAdapter
import com.example.firstretrofit2.databinding.FragmentListAllElementsBinding
import com.example.firstretrofit2.retrofit.API
import com.example.firstretrofit2.retrofit.User
import com.example.firstretrofit2.retrofit.authReuest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListAllElements : Fragment() {
   lateinit var binding:FragmentListAllElementsBinding
   private lateinit var adapter: ProductsAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentListAllElementsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter=ProductsAdapter()
        binding.products.layoutManager=LinearLayoutManager(requireContext())
        binding.products.adapter=adapter
        // Связь с интерфейсом
        val productsApi = retrofit.create(API::class.java)

        var User:User?=null
        viewLifecycleOwner.lifecycleScope.launch {
            User=productsApi.authFun(
                authReuest("emilys","emilyspass")
            )
            MainActivity.binding.toolbar.setTitle(User?.firstName.toString())
        }



        //связь с searchview
        binding.svProduct.setOnQueryTextListener(object : OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Использование корутин для выполнения сетевого запроса
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        // Выполнение сетевого запроса в IO потоке
                        val products = withContext(Dispatchers.IO) {
//                    var list=productsApi.getAllProducts()
                            var list=query?.let{
//                                productsApi.getProductBySearch(it)
                                //с авторизацией пользователя
                                productsApi.getProductBySearchByAuth(User?.token?:" ",it)
                            }
                            adapter.submitList(list?.products)
                        }


                    } catch (e: Exception) {
                        // Обработка ошибок (например, показать тост)
                        e.printStackTrace()
                        // Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }
}