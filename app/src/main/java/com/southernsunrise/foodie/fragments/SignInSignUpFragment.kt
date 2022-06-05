package com.southernsunrise.foodie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.southernsunrise.foodie.R


class SignInSignUpFragment : Fragment() {

    private lateinit var logInButton: Button
    private lateinit var signUpButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_in_sign_up, container, false)

        logInButton = view.findViewById(R.id.btn_login)
        signUpButton = view.findViewById(R.id.btn_signup)

        logInButton.setOnClickListener {
            parentFragment?.findNavController()
                ?.navigate(R.id.action_signInSignUpFragment_to_loginFragment)
        }
       signUpButton.setOnClickListener {
           parentFragment?.findNavController()?.navigate(R.id.action_signInSignUpFragment_to_signupFragment)
       }
        return view
    }


}