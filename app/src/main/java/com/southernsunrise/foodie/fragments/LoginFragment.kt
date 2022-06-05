package com.southernsunrise.foodie.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.southernsunrise.foodie.utilities.Constants
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass


class LoginFragment : Fragment() {

    private lateinit var tv_termsAndPrivacy: TextView
    private lateinit var tv_signUp: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        progressBar = view.findViewById(R.id.progressbar)

        tv_termsAndPrivacy = view.findViewById(R.id.tv_termsAndPrivacy)
        tv_signUp = view.findViewById(R.id.tv_signUp)
        setUpSpannableTexts()
        val forgotPasswordTextView = view.findViewById<TextView>(R.id.tv_forgotPassword)
        forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        val signInButton = view.findViewById<Button>(R.id.btn_logIn)
        emailEditText = view.findViewById<EditText>(R.id.et_email)
        passwordEditText = view.findViewById<EditText>(R.id.et_password)

        signInButton.setOnClickListener {
            if (allFieldsFilled()) {
                logIn()
            }
        }

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(R.layout.progress_dialog_background)
        progressDialog = dialogBuilder.create()


        return view
    }


    private fun logIn() {
        // activity?.let { Constants.showProgressDialog(progressBar, it) }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            emailEditText.text.toString().trim(),
            passwordEditText.text.toString().trim()
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // val firebaseUser: FirebaseUser = it.result!!.user!!
                    FirestoreClass().getUserData(
                        this@LoginFragment,
                        requireActivity() as AppCompatActivity,
                    )
                    setUserLoggedIn()
                    //navigateToMainContainerFragment()


                } else {
                    Toast.makeText(
                        activity,
                        it.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    //  activity?.let { it1 -> Constants.hideProgressBar(progressBar, it1) }
                }
            }
    }


    private fun setUpSpannableTexts() {
        val termsOfUseAndPrivacyPolicySpannableString =
            SpannableString(tv_termsAndPrivacy.text.toString())
        termsOfUseAndPrivacyPolicySpannableString[33..45] = object : ClickableSpan() {

            override fun onClick(p0: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#8EA545")

            }
        }
        termsOfUseAndPrivacyPolicySpannableString[50..64] = object : ClickableSpan() {
            override fun onClick(p0: View) {
                TODO("Not yet implemented")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#8EA545")
            }
        }
        tv_termsAndPrivacy.text = termsOfUseAndPrivacyPolicySpannableString

        val signUpSpannable = SpannableString(tv_signUp.text.toString())
        signUpSpannable[(signUpSpannable.length - 8)..signUpSpannable.length] =
            object : ClickableSpan() {
                override fun onClick(p0: View) {
                    findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
                }

                @SuppressLint("ResourceAsColor")
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#8EA545")
                    ds.isUnderlineText = false
                }

            }
        tv_signUp.text = signUpSpannable
        tv_signUp.movementMethod = LinkMovementMethod()


    }


    private fun allFieldsFilled(): Boolean {
        when {
            TextUtils.isEmpty(emailEditText.text.toString().trim()) -> {
                Toast.makeText(
                    activity,
                    "Please input your email",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            TextUtils.isEmpty(passwordEditText.text.toString().trim()) -> {
                Toast.makeText(activity, "please input your password", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            else -> return true
        }
    }


    private fun setUserLoggedIn() {
        val sharedPrefs = requireActivity().getSharedPreferences(
            Constants.FOODIE_PREFERENCES,
            Context.MODE_PRIVATE
        )
        sharedPrefs.edit().putBoolean(Constants.USER_IS_LOGGED_IN, true).apply()
    }

    fun navigateToMainContainerFragment() {
        parentFragment?.parentFragment?.findNavController()
            ?.navigate(R.id.action_logInSignUpFragment_to_mainContainerFragment)

    }

    fun setDialog(visible: Boolean) {

        if (visible) {
            progressDialog.show()
            progressDialog.setCancelable(false)
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else if (!visible) {
            progressDialog.dismiss()
            activity?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    fun goToProfileCompletionScreen(){
        requireActivity().supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainerView, EditProfileFragment()
        ).commit()
    }




}