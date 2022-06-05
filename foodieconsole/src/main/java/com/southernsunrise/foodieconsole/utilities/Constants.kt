package com.southernsunrise.foodieconsole.utilities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val PRODUCTS_IMAGE: String = "Product_Image"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val PRODUCT_IMAGES = "product_images"
    const val PRODUCTS = "products"
    const val BASE = "base"
    const val PRODUCT_IMAGE = "productImage"
    const val PRODUCT_NAME = "productName"
    const val PRODUCT_ID: String = "productId"
    const val USERS = "users"
    const val USER_CART: String = "cart"
    const val FAVORITE_PRODUCTS = "favoriteProducts"
    const val PRODUCT_PRICE = "productPrice"
    const val PRODUCT_TYPE = "productType"
    const val BEVERAGES = "beverages"
    const val FOOD = "food"
    const val DESSERTS = "desserts"
    const val PRODUCT_CALORIES = "productCalories"
    const val READY_IN_MINUTES = "readyInMinutes"
    const val PRODUCT_DESCRIPTION = "productDescription"
    const val EDIT_PRODUCT = "editProduct"
    const val ADD_PRODUCT = "addProduct"

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

}