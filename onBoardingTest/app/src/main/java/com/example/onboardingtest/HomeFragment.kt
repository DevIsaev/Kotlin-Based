package com.example.onboardingtest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view= inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<TextView>(R.id.textView).text="Пользователь: "+Data.userName.toString()+"\n"+
                "Почта: "+Data.userEmail.toString()+"\n"+
                "Телефон: "+Data.userPhone.toString()+"\n"+
                "Пароль: "+Data.userPass.toString()
        return view
    }


}