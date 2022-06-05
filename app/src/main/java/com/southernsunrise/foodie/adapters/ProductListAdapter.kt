package com.southernsunrise.foodie.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.models.Product
import com.southernsunrise.foodie.utilities.Constants
import com.squareup.picasso.Picasso

class ProductListAdapter(var context: Context, var productList: ArrayList<Product>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return productList.size
    }

    override fun getItem(p0: Int): Any {
        return productList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }


    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item, null)
        val currentProduct: Product = productList[p0]
        val addRemoveFromFavoritesButton: ImageButton =
            view.findViewById(R.id.add_remove_from_favorites)
        val addRemoveFromCartButton: ImageButton = view.findViewById(R.id.add_remove_from_cart)
        val image: ImageView = view.findViewById(R.id.product_image)
        val name: TextView = view.findViewById(R.id.product_name)
        val priceTextView: TextView = view.findViewById(R.id.tv_price)

        name.text = currentProduct.productName
        if (currentProduct.productPrice != null) {
            priceTextView.text = "$${currentProduct.productPrice}"
        }

        Picasso.get().load(currentProduct.productImage).into(image)

        // getting favorite products list and setting list items up according to the results
        // if user has no favorite products yet there will be crated a collection which is empty and successfully get the empty "favoriteProductDocumentsList"
        getFavoriteProductsAndSetupFavoritesButton(currentProduct, addRemoveFromFavoritesButton)
        // same for cart products
        getCartProductsAndSetupFavoritesButton(currentProduct, addRemoveFromCartButton)

        return view
    }

    private fun getFavoriteProductsAndSetupFavoritesButton(
        currentProduct: Product,
        addRemoveFromFavoritesButton: ImageButton
    ) {
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(Constants.FAVORITE_PRODUCTS).get().addOnSuccessListener {
                val favoriteProductDocumentsList = it.documents
                var favoriteProductsIDsList = ArrayList<String>()
                for (productDoc in favoriteProductDocumentsList) {
                    favoriteProductsIDsList.add(productDoc.id)
                }


                if (favoriteProductsIDsList.contains(currentProduct.productId.toString())) {
                    addRemoveFromFavoritesButton.setImageResource(R.drawable.ic_favorite_full)
                } else addRemoveFromFavoritesButton.setImageResource(R.drawable.ic_favorite_blank)

                addRemoveFromFavoritesButton.setOnClickListener {
                    if (!favoriteProductsIDsList.contains(currentProduct.productId.toString())) {
                        addRemoveFromFavoritesButton.setImageResource(R.drawable.ic_favorite_full)
                        FirestoreClass().addProductToFavorites(currentProduct)
                        favoriteProductsIDsList.add(currentProduct.productId.toString())
                    } else {
                        addRemoveFromFavoritesButton.setImageResource(R.drawable.ic_favorite_blank)
                        FirestoreClass().removeProductFromFavorites(currentProduct)
                        favoriteProductsIDsList.remove(currentProduct.productId.toString())

                    }
                }


            }

    }

    private fun getCartProductsAndSetupFavoritesButton(
        currentProduct: Product,
        addRemoveFromCartButton: ImageButton
    ) {
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(Constants.USER_CART).get().addOnSuccessListener {
                val cartProductDocumentsList = it.documents
                var cartProductsIDsList = ArrayList<String>()
                for (productDoc in cartProductDocumentsList) {
                    cartProductsIDsList.add(productDoc.id)
                }


                if (cartProductsIDsList.contains(currentProduct.productId.toString())) {
                    addRemoveFromCartButton.setImageResource(R.drawable.ic_remove_from_cart)
                } else addRemoveFromCartButton.setImageResource(R.drawable.ic_add_to_cart)

                addRemoveFromCartButton.setOnClickListener {
                    if (!cartProductsIDsList.contains(currentProduct.productId.toString())) {
                        addRemoveFromCartButton.setImageResource(R.drawable.ic_remove_from_cart)
                        FirestoreClass().addProductToCart(currentProduct)
                        cartProductsIDsList.add(currentProduct.productId.toString())
                    } else {
                        addRemoveFromCartButton.setImageResource(R.drawable.ic_add_to_cart)
                        FirestoreClass().removeProductFromCart(currentProduct)
                        cartProductsIDsList.remove(currentProduct.productId.toString())

                    }
                }


            }

    }

}