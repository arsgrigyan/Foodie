package com.southernsunrise.foodie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.foodie.R

class ForgotPasswordFragment : Fragment() {

    private lateinit var resetPasswordEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)


        val toolbar = view.findViewById<Toolbar>(R.id.forgotPassword_toolbar)
        toolbar.setNavigationIcon(R.drawable.left_arrow_24)
        activity?.setActionBar(toolbar)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.actionBar?.title = null
        toolbar.setNavigationOnClickListener { view: View? -> requireActivity().onBackPressed() }


        resetPasswordEditText = view.findViewById(R.id.et_resetPassword)
        submitButton = view.findViewById(R.id.btn_submit)

        submitButton.setOnClickListener {
            when {
                resetPasswordEditText.text.toString().trim().isBlank() -> {
                    Toast.makeText(activity, "please input your email", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    sendRecoveryEmail()
                }

            }
        }

        return view
    }

    private fun sendRecoveryEmail(){
        Firebase.auth.sendPasswordResetEmail(
            resetPasswordEditText.text.toString().trim()
        ).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(activity, "Reset email sent", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}