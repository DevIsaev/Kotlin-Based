package com.example.authdummyjson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.authdummyjson.Retrofit.API
import com.example.authdummyjson.Retrofit.LoginView
import com.example.authdummyjson.databinding.FragmentFirstBinding
import com.example.firstretrofit2.retrofit.authReuest
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private val viewModel:LoginView by activityViewModels()
    private lateinit var api:API
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        api=MainActivity.Api

        binding.buttonFirst.setOnClickListener {
            auth(authReuest(binding.username.text.toString(),binding.password.text.toString()))
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            //viewModel.token.value=""
        }
        binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

private fun auth(authReuest: authReuest){
    CoroutineScope(Dispatchers.IO).launch {
        var resp=api.auth(authReuest)
        //resp.errorBody()?.string()
        var message=resp.errorBody()?.string()?.let{ JSONObject(it).getString("message")}
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(),message.toString(),Toast.LENGTH_LONG).show()

            var user=resp.body()
            if(user!=null){
                Picasso.get().load(user.image).into(binding.imageView)
                Toast.makeText(requireContext(),"welcome, ${user.firstName}",Toast.LENGTH_LONG).show()
                binding.RESULT.text=user.firstName+""+user.lastName
                viewModel.token.value=user.token
                binding.buttonNext.visibility=View.VISIBLE
            }
        }
    }
}
}