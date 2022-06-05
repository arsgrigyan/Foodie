package com.southernsunrise.foodie.utilities

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.foodie.fragments.BeveragesFragment
import com.southernsunrise.foodie.fragments.DessertsFragment
import com.southernsunrise.foodie.fragments.FavoritesFragment
import com.southernsunrise.foodie.fragments.FoodFragment
import com.southernsunrise.foodie.models.Product

class GetProducts(val fragment: Fragment) {
    private val mFirestoreDatabase = FirebaseFirestore.getInstance()

    fun getProducts() {

        var productType: String = ""
        val productsList: ArrayList<Product> = ArrayList()

        when (fragment) {
            is FoodFragment -> {
                productType = Constants.FOOD
                fragment.progressbarVisible(true)
            }
            is BeveragesFragment -> {
                productType = Constants.BEVERAGES
                fragment.progressbarVisible(true)

            }
            is DessertsFragment -> {
                productType = Constants.DESSERTS
                fragment.progressbarVisible(true)

            }
        }
        mFirestoreDatabase.collection(Constants.PRODUCTS).document(productType)
            .collection(Constants.BASE).get().addOnSuccessListener {
                val productsDocumentList: MutableList<DocumentSnapshot> = it.documents
                if (productsDocumentList.isNotEmpty()) {
                    for (product in productsDocumentList) {
                        val productName = product.getString(Constants.PRODUCT_NAME)
                        val productImageUrl = product.getString(Constants.PRODUCT_IMAGE)
                        val productPrice =
                            product.get(Constants.PRODUCT_PRICE).toString().toDouble()
                        val productId: Long = product.get(Constants.PRODUCT_ID) as Long
                        val productType: String = product.getString(Constants.PRODUCT_TYPE)!!
                        val productCalories = product.getDouble(Constants.PRODUCT_CALORIES)
                        val readyInMinutes = product.getDouble(Constants.READY_IN_MINUTES)
                        val productDescription = product.getString(Constants.PRODUCT_DESCRIPTION).toString()
                        productsList.add(
                            Product(
                                productImageUrl,
                                productName,
                                productId,
                                productPrice,
                                productType,
                                productCalories,
                                readyInMinutes,
                                productDescription

                            )
                        )
                    }
                    when (fragment) {
                        is FoodFragment -> {
                            fragment.setFood(productsList)
                            fragment.progressbarVisible(false)
                        }
                        is BeveragesFragment -> {
                            fragment.setBeverages(productsList)
                            fragment.progressbarVisible(false)
                        }
                        is DessertsFragment -> {
                            fragment.setDesserts(productsList)
                            fragment.progressbarVisible(false)

                        }
                    }
                }
            }
    }

    fun getFavoritesList(fragment: Fragment) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val favoriteProductsList: ArrayList<Product> = ArrayList()
        mFirestoreDatabase.collection(Constants.USERS).document(currentUser.uid)
            .collection(Constants.FAVORITE_PRODUCTS).get().addOnSuccessListener {
                val favoriteProductsDocumentsList: MutableList<DocumentSnapshot> = it.documents
                if (favoriteProductsDocumentsList.isNotEmpty()) {
                    for (product in favoriteProductsDocumentsList) {
                        val productName = product.getString(Constants.PRODUCT_NAME)
                        val productType = product.getString(Constants.PRODUCT_TYPE)
                        val productId = product.getLong(Constants.PRODUCT_ID)
                        val productPrice = product.getDouble(Constants.PRODUCT_PRICE)
                        val productImageURL = product.getString(Constants.PRODUCT_IMAGE)
                        val productCalories = product.getDouble(Constants.PRODUCT_CALORIES)
                        val readyInMinutes = product.getDouble(Constants.READY_IN_MINUTES)
                        val productDescription = product.getString(Constants.PRODUCT_DESCRIPTION).toString()
                        favoriteProductsList.add(
                            Product(
                                productImageURL,
                                productName,
                                productId,
                                productPrice,
                                productType,
                                productCalories,
                                readyInMinutes,
                                productDescription
                            )
                        )
                        if (fragment is FavoritesFragment) {
                            fragment.setFavoriteProducts(favoriteProductsList)
                        }

                    }
                } else (fragment as FavoritesFragment).noFavorites()
            }
    }

}
