package com.southernsunrise.foodie.onboarding

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.adapters.ViewPagerAdapter
import com.southernsunrise.foodie.onboarding.screens.FirstScreen
import com.southernsunrise.foodie.onboarding.screens.SecondScreen
import com.southernsunrise.foodie.onboarding.screens.ThirdScreen


class ViewPagerFragment : Fragment() {

    lateinit var nextTextView: TextView
    lateinit var previousTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )
        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )


        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        nextTextView = view.findViewById<TextView>(R.id.tv_next)
        previousTextView = view.findViewById(R.id.tv_previous)
        viewPager.adapter = adapter

        previousTextView.setOnClickListener {
            nextTextView.text = "Next"
            viewPager.currentItem = viewPager.currentItem-1
        }



        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == 2){
                    nextTextView.text = "Finish"
                }else nextTextView.text = "Next"
                if(position == 0){
                    previousTextView.visibility = View.GONE
                }else previousTextView.visibility = View.VISIBLE
            }

        })

        nextTextView.setOnClickListener {

            previousTextView.visibility = View.VISIBLE
            when (viewPager.currentItem) {
                0 -> {
                    viewPager.currentItem = 1
                }
                1 -> {
                    viewPager.currentItem = 2
                }
                2 -> {
                   // if (!userLoggedIn()){
                        findNavController().navigate(R.id.action_viewPagerFragment_to_logInSignUpFragment)
                   // } else findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)

                    onBoardingFinished()

                }
            }
        }


        return view
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("boardingFinished", true)
        editor.apply()
    }
    private fun userLoggedIn(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("loginState", MODE_PRIVATE)
        return sharedPref.getBoolean("isLoggedIn", false)
    }


}
