package com.southernsunrise.foodie.fragments

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.models.Product
import com.southernsunrise.foodie.utilities.Constants
import com.squareup.picasso.Picasso

class ProductDetailsFragment(val product: Product) : Fragment() {

    private lateinit var productTitle: TextView
    private lateinit var favoritesButton: ImageButton
    private lateinit var productName: String
    private lateinit var imgUrl: String
    private var favoritesList: ArrayList<Product>? = null
    private var sharedPrefs: SharedPreferences? = null
    private var id: Long? = null
    private lateinit var productFats: TextView
    private lateinit var productProteins: TextView
    private lateinit var productCarbs: TextView

    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var productCaloriesTextView: TextView
    private lateinit var readyInMinutesTextView: TextView
    private lateinit var productImageView: ImageView
    private lateinit var backButton: ImageButton
    private lateinit var removeOrAddToCartButton: Button
    private lateinit var productDescriptionTextView: TextView
    private lateinit var increaseProductAmountImageButton: ImageButton
    private lateinit var reduceProductAmountImageButton: ImageButton
    private lateinit var productAmountTextView: TextView
    private var productAmount: Int = 1
    private var addedToCart: Boolean = false

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)

        backButton = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        productNameTextView = view.findViewById(R.id.et_product_name)
        productCaloriesTextView = view.findViewById(R.id.tv_calories)
        productPriceTextView = view.findViewById(R.id.tv_price)
        readyInMinutesTextView = view.findViewById(R.id.tv_ready_in_minutes)

        productDescriptionTextView = view.findViewById(R.id.et_product_desription)
        productDescriptionTextView.setLineSpacing(0f, 1f)
        productDescriptionTextView.setAutoSizeTextTypeUniformWithConfiguration(
            18, 20, 2, TypedValue.COMPLEX_UNIT_DIP
        )

        productDescriptionTextView.setLineSpacing(5f, 1f)
        productDescriptionTextView.maxLines = 3

        productImageView = view.findViewById(R.id.iv_product_img)
        productImageView.clipToOutline = true
        // setUpViews(product)

        productAmountTextView = view.findViewById(R.id.tv_product_amount)
        increaseProductAmountImageButton = view.findViewById(R.id.btn_increase_amount)
        increaseProductAmountImageButton.setOnClickListener {
            productAmountTextView.text = (++productAmount).toString()
        }

        reduceProductAmountImageButton = view.findViewById(R.id.btn_reduce_amount)
        reduceProductAmountImageButton.setOnClickListener {
            if (productAmount > 1) {
                productAmountTextView.text = (--productAmount).toString()
            }
        }


        removeOrAddToCartButton = view.findViewById(R.id.btn_add_to_cart)
        removeOrAddToCartButton.setOnClickListener {
            if (addedToCart) {
                FirestoreClass().removeProductFromCart(this.product)
                removeOrAddToCartButton.text = "Add to Cart"
                increaseDecreaseButtonsEnabled(true)
                addedToCart = false
            } else {
                FirestoreClass().addProductToCart(product, productAmount)
                productAmountTextView.text = productAmount.toString()
                addedToCart = true
                removeOrAddToCartButton.text = "Remove from Cart"
            }
        }
        /* addToCartButton.setOnClickListener {
             FirestoreClass().addProductToCart(product, productAmount)
         }

         */
        checkIfProductIsInCartAndSetUpView()

        return view
    }

    private fun checkIfProductIsInCartAndSetUpView() {
        val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid)
            .collection(Constants.USER_CART).get().addOnSuccessListener {
                val cartProductsDocumentList: MutableList<DocumentSnapshot> = it.documents
                val cartProductsAmountHashMap: HashMap<Long, Int> = HashMap()
                for (product in cartProductsDocumentList) {
                    cartProductsAmountHashMap[product.id.toLong()] =
                        product.getLong(Constants.PRODUCT_AMOUNT_ADDED_IN_CART)!!.toInt()
                }
                if (cartProductsAmountHashMap.containsKey(this.product.productId)) {
                    removeOrAddToCartButton.text = "Remove from Cart"
                    addedToCart = true
                    increaseDecreaseButtonsEnabled(false)

                    productAmount = cartProductsAmountHashMap[this.product.productId]!!
                    productAmountTextView.text = productAmount.toString()

                } else {
                    removeOrAddToCartButton.text = "Add to Cart"
                    addedToCart = false

                }
            }
        setUpViews(this.product)
    }

    private fun increaseDecreaseButtonsEnabled(isEnabled: Boolean) {
        increaseProductAmountImageButton.isEnabled = isEnabled
        reduceProductAmountImageButton.isEnabled = isEnabled

    }

    private fun setUpViews(product: Product) {
        Picasso.get().load(product.productImage.toString()).into(productImageView)
        productNameTextView.text = product.productName
        productPriceTextView.text = product.productPrice.toString()
        productCaloriesTextView.text = product.productCalories.toString() + " Kcal"
        readyInMinutesTextView.text = product.readyInMinutes?.toInt().toString() + " mins"
        productDescriptionTextView.text = product.productDescription


    }


}



