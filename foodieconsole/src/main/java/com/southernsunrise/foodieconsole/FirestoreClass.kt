package com.southernsunrise.foodieconsole

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.southernsunrise.foodieconsole.utilities.Constants
import com.southernsunrise.foodieconsole.utilities.Product

class FirestoreClass {
    val fireStore = FirebaseFirestore.getInstance()

    fun addProductToDataBase(activity: MainActivity, productDetails: Product) {
        val productImageUri: Uri? = productDetails.productImage as Uri?
        val productName: String = productDetails.productName
        val productType: String =
            productDetails.productType!!
        val productPrice: Double = productDetails.productPrice
        val productID: Long = productDetails.productId!!
        val productCalories = productDetails.productCalories
        val readyInMinutes = productDetails.readyInMinutes
        val productDescription = productDetails.productDescription

        var imageDownloadableURI: String? = null

        if (productImageUri != null) {
            val imageName = Constants.PRODUCTS_IMAGE + System.currentTimeMillis() + "." +
                    Constants.getFileExtension(
                        activity, productImageUri
                    )


            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.PRODUCT_IMAGES
            ).child(imageName)

            val uploadProcess = sRef.putFile(productImageUri)
            uploadProcess.addOnProgressListener {

            }
                .addOnSuccessListener {


                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        imageDownloadableURI = uri.toString()

                        if (activity.allFieldsFilled()) {
                            val productData = Product(
                                productName,
                                productPrice,
                                imageDownloadableURI!!,
                                productType,
                                productID,
                                productCalories,
                                readyInMinutes,
                                productDescription
                            )
                            fireStore.collection(Constants.PRODUCTS).document(productType)
                                .collection(Constants.BASE).document(productID.toString())
                                .set(productData).addOnSuccessListener {
                                    Toast.makeText(
                                        activity,
                                        "Product has been added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    activity.updateRecyclerViews(productType)

                                }.addOnFailureListener { error ->
                                    Toast.makeText(
                                        activity,
                                        error.message.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }


                    }
                        .addOnFailureListener {
                            Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                .addOnFailureListener {
                    activity.removePreviousImageUri()
                }
        }

    }


    fun updateProductDetails(activity: MainActivity, productInfo: HashMap<String, Any>) {
        val productType = productInfo[Constants.PRODUCT_TYPE]
        val productId = productInfo[Constants.PRODUCT_ID]
        fireStore.collection(Constants.PRODUCTS).document(productType.toString())
            .collection(Constants.BASE).document(productId.toString()).update(productInfo)
            .addOnSuccessListener {
                Toast.makeText(
                    activity,
                    "Product info has been updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
                activity.updateRecyclerViews(productType.toString())
            }
    }

    fun uploadNewImageAndUpdateProductDetails(
        activity: MainActivity,
        productInfo: HashMap<String, Any>
    ) {
        val productInformation:HashMap<String, Any> = productInfo
        val productImageUri: Uri = productInformation[Constants.PRODUCT_IMAGE] as Uri
        var imageDownloadableURI = ""
        //   if (productImageUri != null) {
        val imageName = Constants.PRODUCTS_IMAGE + System.currentTimeMillis() + "." +
                Constants.getFileExtension(
                    activity, productImageUri
                )

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.PRODUCT_IMAGES
        ).child(imageName)

        sRef.putFile(productImageUri).addOnSuccessListener {
            it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())
                imageDownloadableURI = uri.toString()
                productInformation[Constants.PRODUCT_IMAGE] = imageDownloadableURI
                updateProductDetails(activity, productInformation)
            }

        }
            .addOnProgressListener {

            }
        //}
    }


    fun removeProductFromDB(activity: MainActivity, product: Product) {

        val productID: Long = product.productId!!
        val productType: String = product.productType!!
        val productImageUrl: String = product.productImage.toString()
        fireStore.collection(Constants.PRODUCTS).document(productType).collection(Constants.BASE)
            .document(productID.toString()).delete().addOnSuccessListener {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.product_remove_success),
                    Toast.LENGTH_SHORT
                ).show()
                removeProductFromAllUsersCartAndFavorites(product)
                removeProductImage(productImageUrl)
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                activity.updateRecyclerViews(productType)

            }
    }

    private fun removeProductImage(productImageUrl: String) {
        val httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(productImageUrl)
        httpsReference.delete().addOnSuccessListener {

        }
    }

    fun removeProductFromAllUsersCartAndFavorites(product: Product) {
        fireStore.collection(Constants.USERS).get().addOnSuccessListener {
            val usersDocumentList: MutableList<DocumentSnapshot> = it.documents
            for (user in usersDocumentList) {
                fireStore.collection(Constants.USERS).document(user.id)
                    .collection(Constants.USER_CART).document(product.productId.toString()).delete()
                    .addOnSuccessListener {
                        fireStore.collection(Constants.USERS).document(user.id)
                            .collection(Constants.FAVORITE_PRODUCTS)
                            .document(product.productId.toString())
                            .delete().addOnSuccessListener {

                            }
                    }
            }

        }


    }
}
