package com.southernsunrise.foodie.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
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
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true


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
            navigateToProfileFragment()
            true
        }
        navigationView.menu.findItem(R.id.myCart).setOnMenuItemClickListener {
            navigateToCartFragment()
            true
        }
        navigationView.menu.findItem(R.id.about_us).setOnMenuItemClickListener {
            navigateAboutUsFragment()
            true
        }
        navigationView.menu.findItem(R.id.share).setOnMenuItemClickListener {
            shareAppDownloadLink()
            true
        }


        val headerLayout = navigationView.getHeaderView(0)
        val headerNameTitleTextView = headerLayout.findViewById<TextView>(R.id.header_title)
        if (getUserName().length > 10) {
            headerNameTitleTextView.text = "Hello,\n${getUserName()}!"
        } else headerNameTitleTextView.text = "Hello, ${getUserName()}!"



        return view
    }

    private fun shareAppDownloadLink() {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Download 'Foodie' here: https://drive.google.com/file/d/1IX0pjQSfzgouSZBI5rU1oNgQnpDTKvtb/view?usp=sharing"
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, null))

    }

    private fun navigateAboutUsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, AboutUsFragment()).addToBackStack(null)
            .setTransition(TRANSIT_FRAGMENT_OPEN).commit()
        drawerLayout.closeDrawer(navigationView)

    }

    private fun navigateToProfileFragment() {
        parentFragment?.findNavController()
            ?.navigate(R.id.action_drawerMainContainerFragment_to_profileScreenFragment)
        drawerLayout.closeDrawer(navigationView)

    }

    private fun navigateToCartFragment() {
        parentFragment?.findNavController()
            ?.navigate(R.id.action_drawerMainContainerFragment_to_cartFragment)
        drawerLayout.closeDrawer(navigationView)
    }

    private fun getUserName(): String {
        val sharedPrefs =
            requireActivity().getSharedPreferences(Constants.FOODIE_PREFERENCES, MODE_PRIVATE)
        return sharedPrefs.getString(Constants.LOGGED_IN_USERNAME, "Username").toString()
    }

}


