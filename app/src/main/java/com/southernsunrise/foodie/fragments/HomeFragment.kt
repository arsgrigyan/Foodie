package com.southernsunrise.foodie.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.southernsunrise.foodie.R

class HomeFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        navController = this.childFragmentManager.findFragmentById(R.id.home_containerView)!!
            .findNavController()
        bottomNavigationView = view.findViewById(R.id.bottomNavView)
        bottomNavigationView.setupWithNavController(navController)



        return view
    }

}