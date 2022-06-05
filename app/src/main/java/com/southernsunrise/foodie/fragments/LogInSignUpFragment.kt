package com.southernsunrise.foodie.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.southernsunrise.foodie.utilities.Constants
import com.southernsunrise.foodie.R

class LogInSignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log_in_sign_up, container, false)

        val sharedPref =
            requireActivity().getSharedPreferences(Constants.FOODIE_PREFERENCES, MODE_PRIVATE)

       if (sharedPref.contains(Constants.USER_IS_LOGGED_IN)) {
            childFragmentManager.findFragmentById(R.id.signUpSignIn_containerView)
                ?.findNavController()?.navigate(
                R.id.action_signInSignUpFragment_to_loginFragment
            )

        }




        return view
    }

}