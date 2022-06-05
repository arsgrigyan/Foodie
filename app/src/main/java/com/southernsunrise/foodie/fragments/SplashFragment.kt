package com.southernsunrise.foodie.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.utilities.Constants

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler().postDelayed({
            if (boardingFinished()) {
                if(!userLoggedIn()){
                    findNavController().navigate(R.id.action_splashFragment_to_logInSignUpFragment)
                }else findNavController().navigate(R.id.action_splashFragment_to_mainContainerFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }, 2500)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun boardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("boardingFinished", false)
    }

    private fun userLoggedIn(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences(Constants.FOODIE_PREFERENCES, MODE_PRIVATE)
        return sharedPref.getBoolean(Constants.USER_IS_LOGGED_IN, false)
    }

}