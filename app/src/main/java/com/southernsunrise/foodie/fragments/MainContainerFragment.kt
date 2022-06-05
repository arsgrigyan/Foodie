package com.southernsunrise.foodie.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.utilities.User

class MainContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_container, container, false)
        requireActivity().window.statusBarColor = Color.parseColor("#adcb52")
        getUserData()

        return view
    }

    private fun getUserData() {
        FirestoreClass().getUserData(this, requireActivity() as AppCompatActivity)
    }

    fun showProfileCompletionScreen(userData: User) {
        if (!userData.profileCompleted) {
             requireActivity().supportFragmentManager.beginTransaction().replace(
                  R.id.fragmentContainerView, EditProfileFragment()
              ).addToBackStack(null).commit()


        }
    }

}
