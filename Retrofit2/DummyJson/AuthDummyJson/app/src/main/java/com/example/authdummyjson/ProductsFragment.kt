package com.example.authdummyjson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authdummyjson.Retrofit.API
import com.example.authdummyjson.Retrofit.LoginView
import com.example.authdummyjson.databinding.FragmentSecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding
    private val viewModel: LoginView by activityViewModels()
    private lateinit var api: API
    private lateinit var adapter: ProductsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        api=MainActivity.Api
        init()


        viewModel.token.observe(viewLifecycleOwner){token->
            CoroutineScope(Dispatchers.IO).launch {
                var list= api.getAllProducts(token)
                requireActivity().runOnUiThread {
                    adapter.submitList(list.products)
                }
            }
        }
    }

    private fun init(){
        adapter=ProductsAdapter()
        binding.products.layoutManager= LinearLayoutManager(requireContext())
        binding.products.adapter=adapter
    }
}