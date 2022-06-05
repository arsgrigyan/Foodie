package com.southernsunrise.foodie.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.southernsunrise.foodie.utilities.Constants
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.utilities.User


class SignupFragment : Fragment() {

    private lateinit var loginTextView: TextView
    lateinit var signUpButton: TextView
    lateinit var emailEditText: TextView
    lateinit var passwordEditText: TextView
    lateinit var nameEditText: TextView
    lateinit var repeatPasswordEditText: TextView
    lateinit var progressDialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val dialogBuilder = AlertDialog.Builder(requireContext())
         dialogBuilder.setView(R.layout.layout_progressbar)
        progressDialog = dialogBuilder.create()

        loginTextView = view.findViewById(R.id.tv_logIn)
        setUpSpannableTexts()

        signUpButton = view.findViewById<Button>(R.id.btn_signUp)
        emailEditText = view.findViewById<EditText>(R.id.et_email)
        passwordEditText = view.findViewById<EditText>(R.id.et_password)
        nameEditText = view.findViewById<EditText>(R.id.et_name)
        repeatPasswordEditText = view.findViewById<EditText>(R.id.et_repeatPassword)

        signUpButton.setOnClickListener {
            when {
                TextUtils.isEmpty(emailEditText.text.toString().trim()) -> {
                    Toast.makeText(
                        activity,
                        "please input your email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(passwordEditText.text.toString().trim()) -> {
                    Toast.makeText(activity, "password cannot be blank", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(nameEditText.text.toString().trim()) -> {
                    Toast.makeText(activity, "Name cannot be blank", Toast.LENGTH_SHORT).show()
                }

                repeatPasswordEditText.text.toString().trim()
                    .isBlank() || repeatPasswordEditText.text.toString() != passwordEditText.text.toString() -> {
                    Toast.makeText(activity, "please confirm your password", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    createUser()
                    logIn()
                }
            }
        }

        return view
    }


    private fun createUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            emailEditText.text.toString().trim(),
            passwordEditText.text.toString().trim()
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val firebaseUser: FirebaseUser = it.result!!.user!!

                    val user = User(
                        firebaseUser.uid,
                        nameEditText.text.toString().trim(),
                        emailEditText.text.toString().trim(),
                        profileCompleted = false,
                        image = ""
                    )

                    FirestoreClass().registerUser(this@SignupFragment, user)
                    //   logIn()
                    setUserLoggedIn()
                    updateUserDisplayName(firebaseUser)
                    sendVerificationEmail(firebaseUser)
                    //  navigateToMainContainerFragment()

                } else Toast.makeText(
                    activity,
                    it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnSuccessListener { logIn() }

    }

    private fun setUserLoggedIn() {
        val sharedPrefs = requireActivity().getSharedPreferences(
            Constants.FOODIE_PREFERENCES,
            Context.MODE_PRIVATE
        )
        sharedPrefs.edit().putBoolean(Constants.USER_IS_LOGGED_IN, true).apply()
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Log.d(TAG, "Email sent.")
                }
            }
    }


    private fun updateUserDisplayName(firebaseUser: FirebaseUser) {
        val profileUpdates = userProfileChangeRequest {
            displayName = nameEditText.text.toString()
            //  photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
        }
        firebaseUser.updateProfile(profileUpdates)

    }


    private fun setUpSpannableTexts() {
        val logInSpannable = SpannableString(loginTextView.text.toString())
        logInSpannable[(logInSpannable.length - 7)..logInSpannable.length] =
            object : ClickableSpan() {

                override fun onClick(p0: View) {
                    findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#8EA545")
                    ds.isUnderlineText = false
                }
            }
        
        loginTextView.text = logInSpannable
        loginTextView.movementMethod = LinkMovementMethod()
    }

    private fun logIn() {
       // activity?.let { Constants.showProgressDialog(progressBar, it) }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            emailEditText.text.toString().trim(),
            passwordEditText.text.toString().trim()
        ).addOnSuccessListener {
            FirestoreClass().getUserData(
                this@SignupFragment,
                requireActivity() as AppCompatActivity,
            )
        }
            .addOnFailureListener {
                // Toast.makeText(activity, "Cant log in, try later", Toast.LENGTH_LONG).show()
            }
    }

    fun navigateToMainContainerFragment() {
        parentFragment?.parentFragment?.findNavController()
            ?.navigate(R.id.action_logInSignUpFragment_to_mainContainerFragment)

    }

    fun goToProfileCompletionScreen(){
        requireActivity().supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainerView, EditProfileFragment()
        ).commit()
    }

    fun setDialog(visible: Boolean) {

        if (visible) {
            progressDialog.show()
            progressDialog.setCancelable(false)
            progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
}