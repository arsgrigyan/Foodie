package com.southernsunrise.foodie.fragments

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.utilities.Constants
import com.southernsunrise.foodie.utilities.User
import java.io.IOException


class EditProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var userNameTextView: TextView
    private lateinit var userPhotoImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var closeEditProfileFragmentButton: ImageButton
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var radioButtonF: RadioButton
    private lateinit var radioButtonM: RadioButton
    private lateinit var progressbar: ProgressBar

    //  private lateinit var mobileCountryCodeEditText: EditText
    private lateinit var mobileNumberEditText: EditText
    private lateinit var mMobileNumber: String
    private lateinit var mCountryCode: String

    private lateinit var userData: User
    private var imageFileURI: Uri? = null
    private var mUserImageURL: String = ""
    private lateinit var progressDialog: AlertDialog
    private lateinit var userProfileImageForegroundProgressLayer: RelativeLayout
    private lateinit var countryCodePicker: CountryCodePicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.layout_progressbar)
        progressDialog = builder.create()
        countryCodePicker = view.findViewById(R.id.country_code_picker)


        userNameTextView = view.findViewById(R.id.tv_username)
        userNameTextView.text = getUserName()
        userPhotoImageView = view.findViewById(R.id.iv_user_photo)
        userPhotoImageView.setOnClickListener(this@EditProfileFragment)
        nameEditText = view.findViewById(R.id.et_name)
        emailEditText = view.findViewById(R.id.et_email)
        genderRadioGroup = view.findViewById(R.id.gender_radioGroup)
        radioButtonM = genderRadioGroup.findViewById(R.id.rb_m)
        radioButtonF = genderRadioGroup.findViewById(R.id.rb_f)
        closeEditProfileFragmentButton = view.findViewById(R.id.close_edit_profile_fragment)
        closeEditProfileFragmentButton.setOnClickListener {
            requireActivity().onBackPressed()
        }


        saveChangesButton = view.findViewById(R.id.btn_saveChanges)
        saveChangesButton.setOnClickListener(this@EditProfileFragment)
        progressbar = view.findViewById(R.id.progressbar)

        mobileNumberEditText = view.findViewById(R.id.mobile_number)
        //  mobileCountryCodeEditText = view.findViewById(R.id.country_code)

        userProfileImageForegroundProgressLayer =
            view.findViewById(R.id.profile_image_progress_foreground_layer)

        userData = User()
        countryCodePicker.registerCarrierNumberEditText(mobileNumberEditText)
        getAndSetUserData()


        return view
    }


    override fun onClick(p0: View?) {
        if (p0 != null) {

            when (p0.id) {

                R.id.iv_user_photo -> {
                    showImageChooserOrAskForPermission()
                }
                R.id.btn_saveChanges -> {
                    updateUserInfo()
                }

            }
        }

    }

    private fun showImageChooserOrAskForPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Constants.showImageChooser(this)
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                Constants.showImageChooser(this)
            } else {
                Constants.showSnackBar(
                    requireView(),
                    getString(R.string.read_storage_permission_denied),
                    true
                )

            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        imageFileURI = data.data!!
                        // updateUserProfilePhoto(imageFileURI!!)
                        FirestoreClass().uploadImageToCloudStorage(this, imageFileURI)
                        //  Constants.showSnackBar(requireView(), getString(R.string.image_applied))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Constants.showSnackBar(
                            requireView(),
                            getString(R.string.image_selection_failed),
                            true
                        )
                    }


                }

            }
        }


    }


    private fun updateUserInfo() {

        if (allFieldsFilledCorrectly()) {

            val username = nameEditText.text.toString()
            val userInfo = hashMapOf<String, Any?>()
            if (username != getUserName()) {
                userInfo[Constants.USERNAME] = username
                userNameTextView.text = username
                userData.username = username
            }

            val mobileNumber = mobileNumberEditText.text.toString().replace(" ", "")
            val countryCode = countryCodePicker.selectedCountryCodeWithPlus.toString()
            var userMobileNumber = ""

            if (mobileNumber.isNotBlank() && countryCode.isNotBlank()) {
                userMobileNumber = "$countryCode $mobileNumber"
            }



            if (userData.profileCompleted) {
                if (mobileNumber != mMobileNumber || countryCode != mCountryCode) {
                    userInfo[Constants.USER_MOBILE] = userMobileNumber
                }
            } else userInfo[Constants.USER_MOBILE] = userMobileNumber


            val gender = if (radioButtonM.isChecked) {
                Constants.MALE
            } else Constants.FEMALE

            if (userData.gender != gender) {
                userInfo[Constants.GENDER] = gender
                userData.gender = gender
            }

            if (!userData.profileCompleted) {
                userInfo[Constants.PROFILE_COMPLETED] = true
            }

            FirestoreClass().updateUserProfileInfo(
                this,
                requireActivity() as AppCompatActivity,
                userInfo,
                userData.profileCompleted
            )


        }
    }


    private fun getAndSetUserData() {
        FirestoreClass().getUserData(this, requireActivity() as AppCompatActivity)
    }

    private fun setUserProfilePhoto(imageURL: String) {
        Constants.setImage(
            this,
            userProfileImageForegroundProgressLayer,
            userPhotoImageView,
            imageURL
        )
    }

    private fun allFieldsFilledCorrectly(): Boolean {
        if (nameEditText.text.toString().trim().isBlank() || emailEditText.text.toString().trim()
                .isBlank() || mobileNumberEditText.text.toString().trim().isBlank()
        ) {
            Toast.makeText(activity, "Username must not be empty", Toast.LENGTH_SHORT).show()
        }

        if(!countryCodePicker.isValidFullNumber){
            Toast.makeText(requireContext(), R.string.number_inccorect, Toast.LENGTH_SHORT).show()
        }


        return (nameEditText.text.toString()
            .isNotEmpty()
                && countryCodePicker.isValidFullNumber &&
                emailEditText.text.toString().isNotEmpty())
    }


    fun setKnownData(data: User) {

        userData = data
        nameEditText.setText(userData.username)
        emailEditText.setText(userData.email)

        if (userData.mobile.isNotEmpty()) {
            val mobileNumberCharactersList: List<String> = userData.mobile.split(" ")

            mMobileNumber = mobileNumberCharactersList[1].replace(" ", "")

            mCountryCode = mobileNumberCharactersList[0]


            val fullNum = userData.mobile.replace(" ", "")

            countryCodePicker.fullNumber = fullNum
        }



        when (userData.gender) {
            Constants.FEMALE -> {
                genderRadioGroup.check(R.id.rb_f)
            }
            else -> genderRadioGroup.check(R.id.rb_m)

        }

        if (userData.profileCompleted) {
            saveChangesButton.text = getString(R.string.save)
        } else saveChangesButton.text = getString(R.string.complete_profile)

        if (userData.image.isNotEmpty()) {
            setUserProfilePhoto(userData.image)
        }

    }


    fun showProgressDialog() {
        progressDialog.show()
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }


    fun hideProgressDialog() {
        progressDialog.dismiss()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    fun onUserProfilePhotoUploadSuccess(profileImageUri: String) {

        mUserImageURL = profileImageUri
        val info = hashMapOf<String, Any?>()
        info.put(Constants.IMAGE, mUserImageURL)
        setUserProfilePhoto(mUserImageURL)
        FirestoreClass().updateUserProfileInfo(
            null,
            null,
            info,
            false
        )
        Constants.showSnackBar(requireView(), getString(R.string.image_applied))
    }

    private fun getUserName(): String {
        val sharedPrefs =
            requireActivity().getSharedPreferences(Constants.FOODIE_PREFERENCES, MODE_PRIVATE)
        return sharedPrefs.getString(Constants.LOGGED_IN_USERNAME, "Username").toString()
    }
}