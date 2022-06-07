package com.southernsunrise.foodie.utilities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.fragments.EditProfileFragment
import com.southernsunrise.foodie.fragments.ProfileFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

object Constants {
    const val PAYMENT_METHOD_MASTERCARD: String = "mastercard"
    const val PAYMENT_METHOD_VISA: String = "visa"
    const val PAYMENT_METHOD_PAYPAL: String = "paypal"
    const val ADDRESS_HOME: String = "homeAddress"
    const val ADDRESS_OFFICE: String = "officeAddress"
    const val CREDIT_CARD: String = "creditCard"


    const val CHECKED_CASH_FREE_PAYMENT_TYPE = "checkedCashFreePaymentType"
    const val CHECKED_PAYMENT_TYPE_CASH_FREE: String = "cashFree"

    const val CHECKED_ADDRESS_TYPE_HOME = "home"
    const val CHECKED_ADDRESS_TYPE_OFFICE = "office"
    const val CHECKED_ADDRESS_TYPE = "checkedAddressType"
    const val CHECKED_PAYMENT_TYPE = "checkedPaymentType"
    const val CHECKED_PAYMENT_TYPE_CASH = "cash"
    const val PRODUCT_AMOUNT_ADDED_IN_CART: String = "productAmountInCart"
    const val USER_CART: String = "cart"
    const val PRODUCT_TYPE: String = "productType"
    const val USER_IMAGES = "user_images"
    const val USERS = "users"
    const val FAVORITE_PRODUCTS = "favoriteProducts"
    const val FOODIE_PREFERENCES = "FoodiePreferences"
    const val LOGGED_IN_USERNAME = "logged_in_username"
    const val USER_IS_LOGGED_IN = "isLoggedIn"
    const val USERNAME = "username"
    const val MALE = "male"
    const val FEMALE = "female"
    const val GENDER = "gender"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val USER_PROFILE_IMAGE = "User_Profile_Image"
    const val USER_MOBILE = "mobile"
    const val IMAGE = "image"
    const val PROFILE_COMPLETED = "profileCompleted"
    const val PRODUCTS = "products"
    const val BEVERAGES = "beverages"
    const val FOOD = "food"
    const val DESSERTS = "desserts"
    const val BASE = "base"
    const val PRODUCT_IMAGE = "productImage"
    const val PRODUCT_NAME = "productName"
    const val PRODUCT_PRICE = "productPrice"
    const val PRODUCT_ID = "productId"
    const val PRODUCT_CALORIES = "productCalories"
    const val READY_IN_MINUTES = "readyInMinutes"
    const val PRODUCT_DESCRIPTION = "productDescription"

    fun showImageChooser(fragment: Fragment) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        fragment.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun showSnackBar(view: View, message: String, error: Boolean = false) {
        val snackBarBackgroundTint = if (error) {
            Color.parseColor("#FF9494")
        } else Color.parseColor("#FFC2E163")

        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(snackBarBackgroundTint).setAnimationMode(ANIMATION_MODE_SLIDE).show()
    }

    fun setImage(
        fragment: Fragment,
        progressForegroundLayer: View,
        imageView: ImageView,
        URL: String
    ) {
        imageView.clipToOutline = true

        progressForegroundLayer.visibility = View.VISIBLE
        if (fragment is EditProfileFragment) {
            progressForegroundLayer.findViewById<ProgressBar>(R.id.profile_image_progress_foreground_progressbar).visibility =
                View.VISIBLE
            progressForegroundLayer.findViewById<ImageView>(R.id.iv_change_photo).visibility =
                View.GONE
        }

        Picasso.get().load(URL).noPlaceholder().into(imageView, object : Callback {
            override fun onSuccess() {
                when (fragment) {
                    is EditProfileFragment -> {
                        progressForegroundLayer.findViewById<ProgressBar>(R.id.profile_image_progress_foreground_progressbar).visibility =
                            View.GONE
                        progressForegroundLayer.findViewById<ImageView>(R.id.iv_change_photo).visibility =
                            View.VISIBLE
                    }
                    is ProfileFragment -> {
                        progressForegroundLayer.visibility = View.GONE
                    }
                }

            }

            override fun onError(e: Exception?) {

                when (fragment) {
                    is EditProfileFragment -> {
                        progressForegroundLayer.findViewById<ProgressBar>(R.id.profile_image_progress_foreground_progressbar).visibility =
                            View.GONE
                        progressForegroundLayer.findViewById<ImageView>(R.id.iv_change_photo).visibility =
                            View.VISIBLE

                    }
                    is ProfileFragment -> {
                        progressForegroundLayer.visibility = View.GONE

                    }
                }
            }

        })
    }
}