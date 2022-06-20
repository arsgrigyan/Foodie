package com.southernsunrise.foodie.firebase

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.fragments.*
import com.southernsunrise.foodie.models.Product
import com.southernsunrise.foodie.utilities.Constants
import com.southernsunrise.foodie.utilities.User

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    private val mFirebaseStorage = FirebaseStorage.getInstance()

    fun registerUser(fragment: SignupFragment, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // By default payment type would be mastercard which user can later change
                FirebaseFirestore.getInstance().collection(Constants.USERS)
                    .document(getCurrentUserId()).update(
                        hashMapOf<String, Any>(Constants.CHECKED_CASH_FREE_PAYMENT_TYPE to Constants.PAYMENT_METHOD_MASTERCARD)
                    )
            }
            .addOnFailureListener {
            }
    }

    fun removeUserData(fragment: Fragment, currentUser: FirebaseUser?, imageFileURL: String) {
        currentUser?.let {
            mFireStore.collection(Constants.USERS).document(it.uid).delete()
                .addOnFailureListener {
                    Log.i("error", it.message.toString())
                }
                .addOnSuccessListener {
                    (fragment as ProfileFragment).deleteUser()
                }
            if (imageFileURL.isNotEmpty()) {
                removeUserImageFromCloudStorage(fragment, imageFileURL)
            }
        }
    }

    fun updateUserProfileInfo(
        fragment: Fragment?,
        activity: AppCompatActivity?,
        userInfo: HashMap<String, Any?>,
        profileWasCompleted: Boolean
    ) {
        if (userInfo.isNotEmpty()) {
            if (fragment is EditProfileFragment) {
                fragment.showProgressDialog()
            }
            val user = FirebaseAuth.getInstance().currentUser!!
            mFireStore.collection(Constants.USERS)
                .document(user.uid)
                .update(userInfo)
                .addOnSuccessListener {

                    if (fragment != null && activity != null) {

                        //updateUserInfoToSharedPrefs(activity, userInfo)
                        if (userInfo[Constants.USERNAME] != null) {
                            saveUsernameToSharedPrefs(
                                fragment,
                                activity,
                                userInfo[Constants.USERNAME].toString()
                            )
                        }
                        if (fragment is EditProfileFragment) {
                            fragment.hideProgressDialog()

                            if (!profileWasCompleted) {
                                activity.onBackPressed()

                            } else {
                                activity.onBackPressed()
                                Constants.showSnackBar(
                                    fragment.requireView(),
                                    activity.getString(R.string.profile_info_changed)
                                )
                            }
                        }
                    }

                }
                .addOnFailureListener { error ->

                    fragment?.let {
                        Constants.showSnackBar(
                            fragment.requireView(),
                            error.message.toString(),
                            true
                        )
                    }
                }

        } else {
            if (fragment != null && activity != null) {
                Constants.showSnackBar(
                    fragment.requireView(),
                    activity.getString(R.string.profile_info_not_changed),
                    true
                )
            }
        }
    }


    fun getUserData(fragment: Fragment, activity: AppCompatActivity) {

        when (fragment) {
            is LoginFragment -> fragment.setDialog(true)
            is SignupFragment -> fragment.setDialog(true)
            is EditProfileFragment -> fragment.showProgressDialog()
            //is ProfileFragment ->  fragment.setUserProfileImageProgressLayer(true)

        }

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                when (fragment) {
                    is LoginFragment -> {
                        saveUsernameToSharedPrefs(fragment, activity, user.username)
                        hideProgressDialog(fragment)
                        if (user.profileCompleted) {
                            (fragment as LoginFragment).navigateToMainContainerFragment()
                        } else {
                            (fragment as LoginFragment).goToProfileCompletionScreen()
                        }

                    }
                    is SignupFragment -> {
                        saveUsernameToSharedPrefs(fragment, activity, user.username)
                        hideProgressDialog(fragment)
                        (fragment as SignupFragment).navigateToMainContainerFragment()

                    }
                    is EditProfileFragment -> {
                        (fragment as EditProfileFragment).setKnownData(user)
                        hideProgressDialog(fragment)
                    }
                    is ProfileFragment -> {
                        (fragment as ProfileFragment).setUserData(user)
                        // fragment.setUserProfileImageProgressLayer(true)
                    }
                    is MainContainerFragment -> {
                        (fragment as MainContainerFragment).showProfileCompletionScreen(user)
                    }
                }


            }
            .addOnFailureListener {
                //Constants.hideProgressBar(progressbar, activity)
                Toast.makeText(
                    activity,
                    it.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()

            }

    }

    private fun saveUsernameToSharedPrefs(
        fragment: Fragment,
        activity: AppCompatActivity,
        username: String
    ) {
        val sharedPrefs = activity
            .getSharedPreferences(Constants.FOODIE_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPrefs?.edit()
        editor?.putString(Constants.LOGGED_IN_USERNAME, username)
        editor?.apply()

    }


    fun uploadImageToCloudStorage(fragment: Fragment, imageFileURI: Uri?) {

        if (imageFileURI != null) {
            val imageName = Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." +
                    Constants.getFileExtension(
                        fragment.requireActivity(), imageFileURI
                    )
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.USER_IMAGES
            ).child(imageName)

            val uploadProcess = sRef.putFile(imageFileURI)
            uploadProcess.addOnProgressListener {
            }
                .addOnSuccessListener {
                    removeUserPreviousImageFromCloudStorage()

                    Log.e(
                        "Firebase Image URL",
                        it.metadata!!.reference!!.downloadUrl.toString()
                    )

                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        (fragment as EditProfileFragment).onUserProfilePhotoUploadSuccess(uri.toString())
                        Constants.showSnackBar(fragment.requireView(), "upload finished")

                    }


                }
                .addOnFailureListener {
                    Constants.showSnackBar(fragment.requireView(), it.message.toString(), true)
                }
            fragment.requireActivity().onBackPressedDispatcher.addCallback {
                if (uploadProcess.isInProgress) {
                    uploadProcess.cancel()
                } else fragment.requireActivity().onBackPressed()
            }

        }
    }

    fun addProductToFavorites(product: Product) {
        val productID = product.productId
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid)
                .collection(Constants.FAVORITE_PRODUCTS).document(productID.toString()).set(product)
        }
    }

    fun removeProductFromFavorites(product: Product) {
        val productID = product.productId
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid)
                .collection(Constants.FAVORITE_PRODUCTS).document(productID.toString()).delete()
        }
    }


    private fun getCurrentUserId(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""

        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId

    }

    private fun hideProgressDialog(fragment: Fragment) {
        when (fragment) {
            is LoginFragment -> {
                (fragment as LoginFragment).setDialog(false)

            }
            is SignupFragment -> {
                (fragment as SignupFragment).setDialog(false)

            }
            is EditProfileFragment -> {
                (fragment as EditProfileFragment).hideProgressDialog()
            }
        }
    }

    fun removeUserImageFromCloudStorage(fragment: Fragment, imageFileURL: String) {
        val httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageFileURL)
        httpsReference.delete().addOnSuccessListener {
        }
    }

    fun removeUserPreviousImageFromCloudStorage() {
        var userImageLink = ""
        val currentUser = FirebaseAuth.getInstance().currentUser
        mFireStore.collection(Constants.USERS).document(currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it != null) {
                    val user = it.toObject(User::class.java)!!
                    userImageLink = user.image
                    if (userImageLink.isNotBlank()) {
                        mFirebaseStorage.getReferenceFromUrl(userImageLink)
                            .delete().addOnSuccessListener {

                            }
                    }
                }
            }

    }

    fun addProductToCart(product: Product, productAmountAddedToCart: Int = 1) {
        val productInfo = hashMapOf<String, Any>(
            Constants.PRODUCT_NAME to product.productName!!,
            Constants.PRODUCT_PRICE to product.productPrice!!,
            Constants.PRODUCT_TYPE to product.productType!!,
            Constants.PRODUCT_ID to product.productId!!,
            Constants.PRODUCT_CALORIES to product.productCalories!!,
            Constants.READY_IN_MINUTES to product.readyInMinutes!!,
            Constants.PRODUCT_DESCRIPTION to product.productDescription!!,
            Constants.PRODUCT_IMAGE to product.productImage!!,
            Constants.PRODUCT_AMOUNT_ADDED_IN_CART to productAmountAddedToCart

        )
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .collection(Constants.USER_CART).document(product.productId.toString()).set(productInfo)
            .addOnSuccessListener {
            }
    }

    fun removeProductFromCart(product: Product) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .collection(Constants.USER_CART).document(product.productId.toString()).delete()
            .addOnSuccessListener {

            }
    }

    fun updateProductAmountInCart(fragment: Fragment, product: Product, amount: Int) {
        val info = hashMapOf<String, Any>(Constants.PRODUCT_AMOUNT_ADDED_IN_CART to amount)
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .collection(Constants.USER_CART).document(product.productId.toString()).update(info)
            .addOnFailureListener {
                Toast.makeText(fragment.requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun setOrUpdateUserAddresses(address: String, addressType: String) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(hashMapOf<String, Any>(addressType to address))
    }

    fun setOrUpdateCheckedAddress(checkedAddressType: String) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(hashMapOf<String, Any>(Constants.CHECKED_ADDRESS_TYPE to checkedAddressType))
    }

    fun setOrUpdateCreditCardInfo(creditCardCode: Long) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(hashMapOf<String, Any>(Constants.CREDIT_CARD to creditCardCode))
    }

    fun setOrUpdateCheckedCashFreePaymentType(paymentType: String) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(hashMapOf<String, Any>(Constants.CHECKED_CASH_FREE_PAYMENT_TYPE to paymentType))
    }

    fun setOrUpdateCheckedPaymentMethod(checkedPaymentType: String) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(hashMapOf<String, Any>(Constants.CHECKED_PAYMENT_TYPE to checkedPaymentType))
    }


}



