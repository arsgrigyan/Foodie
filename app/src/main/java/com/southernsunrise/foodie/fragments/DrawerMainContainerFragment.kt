package com.southernsunrise.foodie.fragments

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.utilities.Constants


class DrawerMainContainerFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_drawer_main_container, container, false)
        val window: Window = requireActivity().window
        window.statusBarColor = Color.parseColor("#adcb52")
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightStatusBars = false


            val toolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolBar)

        drawerLayout = view.findViewById(R.id.drawerLayout)
        navigationView = view.findViewById(R.id.nav_view)
        navController =
            this.childFragmentManager.findFragmentById(R.id.home_containerView)!!
                .findNavController()

        navigationView.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setupWithNavController(toolBar, navController, appBarConfiguration)


        navigationView.menu.findItem(R.id.profile).setOnMenuItemClickListener {
            parentFragment?.findNavController()
                ?.navigate(R.id.action_drawerMainContainerFragment_to_profileScreenFragment)
            drawerLayout.closeDrawer(navigationView)
            true
        }
        navigationView.menu.findItem(R.id.myCart).setOnMenuItemClickListener {
            parentFragment?.findNavController()
                ?.navigate(R.id.action_drawerMainContainerFragment_to_cartFragment)
            drawerLayout.closeDrawer(navigationView)

            true
        }


        val headerLayout = navigationView.getHeaderView(0)
        headerLayout.findViewById<ImageView>(R.id.imageView).visibility = View.GONE
        val headerTitleTextView = headerLayout.findViewById<TextView>(R.id.header_title)
        headerTitleTextView.text = "Hello, ${getUserName()} !"


        return view
    }

    private fun navigateToProfileFragment() {
        parentFragment?.findNavController()
            ?.navigate(R.id.action_drawerMainContainerFragment_to_profileScreenFragment)
        drawerLayout.closeDrawer(navigationView)

    }

    private fun getUserName(): String {
        val sharedPrefs =
            requireActivity().getSharedPreferences(Constants.FOODIE_PREFERENCES, MODE_PRIVATE)
        return sharedPrefs.getString(Constants.LOGGED_IN_USERNAME, "Username").toString()
    }

}


