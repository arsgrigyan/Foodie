package com.southernsunrise.foodie.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.adapters.ProfileListAdapter
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.models.ProfileListItem
import com.southernsunrise.foodie.utilities.Constants
import com.southernsunrise.foodie.utilities.User


class ProfileFragment : Fragment() {

    private lateinit var profileItemListView: ListView
    private lateinit var usernameTextView: TextView
    private lateinit var signOutButton: Button
    private lateinit var deleteAccountTextView: TextView
    private lateinit var editProfileView: LinearLayout
    private lateinit var userPhotoImageView: ImageView
    private lateinit var userPhotoImageViewProgressForegroundLayer: RelativeLayout
    private lateinit var userData: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        activity?.setActionBar(toolbar)
        activity?.actionBar?.title = null
        val backButton = toolbar.findViewById<ImageButton>(R.id.back_arrow)
        backButton.setOnClickListener { requireActivity().onBackPressed() }

        userData = User()

        editProfileView = view.findViewById(R.id.edit_profile)
        editProfileView.setOnClickListener {
            navigateToEditProfileFragment()
        }
        userPhotoImageView = view.findViewById(R.id.iv_user_photo)
        userPhotoImageViewProgressForegroundLayer =
            view.findViewById(R.id.profile_image_progress_foreground)
        userPhotoImageViewProgressForegroundLayer.clipToOutline = true

        usernameTextView = view.findViewById(R.id.tv_username)
        usernameTextView.text = getUserName()

        getUserData()

        profileItemListView = view.findViewById<ListView>(R.id.profile_listView)
        profileItemListView.divider = null
        val profileList = ArrayList<ProfileListItem>()
        profileList.add(ProfileListItem(R.drawable.ic_history, "History"))
        profileList.add(ProfileListItem(R.drawable.ic_favorite_full, "Favorite"))
        profileList.add(ProfileListItem(R.drawable.ic_wallet, "Payment method"))
        val profileListAdapter = ProfileListAdapter(requireContext(), profileList)
        profileItemListView.adapter = profileListAdapter

        signOutButton = view.findViewById(R.id.btn_signOut)
        signOutButton.setOnClickListener {
            signOut()
        }

        deleteAccountTextView = view.findViewById(R.id.tv_deleteAccount)
        deleteAccountTextView.setOnClickListener {
            setAccountDeleteDialog()
        }


        return view
    }


    private fun getUserData() {
        FirestoreClass().getUserData(this, requireActivity() as AppCompatActivity)
    }

    fun setUserData(data: User) {
        userData = data
        setUserProfilePhoto(data.image)
    }

    private fun setAccountDeleteDialog() {

        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setTitle("Delete your account?")
            .setMessage("all your data will be permanently deleted")
            .setPositiveButton(
                "yes, delete my account",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    deleteUser()
                })
            .setNegativeButton("cancel", null)
            // Create the AlertDialog object and return it
            .show()
    }

    private fun deleteUser() {
        val user = Firebase.auth.currentUser!!
        try {
            FirestoreClass().removeUserData(this, user, userData.image)
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Constants.showSnackBar(requireView(), getString(R.string.account_deleted))
                        signOut()
                    }
                }
        } catch (e: Exception) {
            Log.i("deletion error", e.message.toString())
        }
    }


    private fun getUserName(): String {
        val sharedPrefs =
            requireActivity().getSharedPreferences(
                Constants.FOODIE_PREFERENCES,
                Context.MODE_PRIVATE
            )
        return sharedPrefs.getString(Constants.LOGGED_IN_USERNAME, "Username").toString()
    }

    private fun setUserProfilePhoto(imageURL: String) {
        if (imageURL.isNotEmpty()) {
            Constants.setImage(
                this,
                userPhotoImageViewProgressForegroundLayer,
                userPhotoImageView,
                imageURL
            )
        }
    }


    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        navigateToLogInSignUpFragment()
        val sharedPrefs = requireActivity().getSharedPreferences(
            Constants.FOODIE_PREFERENCES,
            Context.MODE_PRIVATE
        )
        sharedPrefs.edit().putBoolean(Constants.USER_IS_LOGGED_IN, false).apply()

        requireActivity().window.statusBarColor = Color.parseColor("#96be20")
    }

    private fun navigateToEditProfileFragment() {
        this.findNavController()
            .navigate(R.id.action_profileFragment_to_editProfileFragment)
    }

    private fun navigateToLogInSignUpFragment() {
        //this(ProfileFragment)-> parent(androidx.navigation.fragment.NavHostFragment) -> parent(ProfileScreenFragment) -> parent(NavHostFragment) -> parent(NavHostFragment) -> parent(MainContainerFragment) -> parent (NavHostFragment)
        parentFragment?.parentFragment?.parentFragment?.parentFragment?.parentFragment?.findNavController()
            ?.navigate(R.id.action_mainContainerFragment_to_logInSignUpFragment)

    }

    fun setUserProfileImageProgressLayer(visible: Boolean) {
        if (visible) {
            userPhotoImageViewProgressForegroundLayer.visibility = View.VISIBLE
        } else userPhotoImageViewProgressForegroundLayer.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        getUserData()
    }
}