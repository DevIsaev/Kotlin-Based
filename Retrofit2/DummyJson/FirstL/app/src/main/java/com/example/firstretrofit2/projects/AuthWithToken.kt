package com.example.firstretrofit2.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firstretrofit2.MainActivity.Companion.retrofit
import com.example.firstretrofit2.R
import com.example.firstretrofit2.databinding.FragmentAuthWithTokenBinding
import com.example.firstretrofit2.databinding.FragmentListAllElementsBinding
import com.example.firstretrofit2.retrofit.API


class AuthWithToken : Fragment() {

    lateinit var binding: FragmentAuthWithTokenBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentAuthWithTokenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productsApi = retrofit.create(API::class.java)
    }

}