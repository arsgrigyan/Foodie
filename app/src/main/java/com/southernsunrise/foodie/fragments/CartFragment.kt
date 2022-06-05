package com.southernsunrise.foodie.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.adapters.RecyclerViewAdapter
import com.southernsunrise.foodie.models.Product
import com.southernsunrise.foodie.utilities.Constants
import java.math.RoundingMode
import java.text.DecimalFormat


class CartFragment : Fragment() {

    private lateinit var cartProductsRecyclerView: RecyclerView
    private lateinit var scrollView: ScrollView
    private lateinit var totalPayAmountTextView: TextView
    private lateinit var backImageButton: ImageButton
    private lateinit var checkoutButton: Button
    private var wholePayAmount: Double = 0.0
    private var productAmount:Int = 0
    private lateinit var productAmountsHashMap:HashMap<Long, Any>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val window =  activity?.window!!
        window?.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightStatusBars = true


        cartProductsRecyclerView = view.findViewById(R.id.cart_recyclerView)
        scrollView = view.findViewById(R.id.scrollView)
        totalPayAmountTextView = view.findViewById(R.id.total_amount)
        backImageButton = view.findViewById(R.id.ib_back_arrow)
        backImageButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        checkoutButton = view.findViewById(R.id.btn_checkout)
        checkoutButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, OrderFragment(roundOffDecimal(wholePayAmount)!!)).addToBackStack(null)
                .setTransition(TRANSIT_FRAGMENT_OPEN).commit()
            wholePayAmount = 0.0

        }
        getCartProductsAndSetRecyclerView()

        return view
    }


    fun getCartProductsAndSetRecyclerView() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid)
            .collection(Constants.USER_CART).get().addOnSuccessListener {
                val cartProductsDocumentsList: MutableList<DocumentSnapshot> = it.documents
                val cartList: ArrayList<Product> = ArrayList()
                productAmountsHashMap = HashMap()
                for (product in cartProductsDocumentsList) {
                    cartList.add(
                        Product(
                            product.get(Constants.PRODUCT_IMAGE).toString(),
                            product.get(Constants.PRODUCT_NAME).toString(),
                            product.getLong(Constants.PRODUCT_ID),
                            product.getDouble(Constants.PRODUCT_PRICE),
                            product.getString(Constants.PRODUCT_TYPE),
                            product.getDouble(Constants.PRODUCT_CALORIES),
                            product.getDouble(Constants.READY_IN_MINUTES),
                            product.getString(Constants.PRODUCT_DESCRIPTION),
                        )

                    )
                    productAmountsHashMap[product.id.toLong()] = product.getLong(Constants.PRODUCT_AMOUNT_ADDED_IN_CART)!!.toInt()
                    wholePayAmount += product.getDouble(Constants.PRODUCT_PRICE)!! * (productAmountsHashMap[product.id.toLong()] as Int)
                }
                totalPayAmountTextView.text = roundOffDecimal(wholePayAmount).toString()
                if (cartList.isNotEmpty()) {
                    setupRecyclerView(cartList)
                }
            }
    }
    private fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun setupRecyclerView(cartList: ArrayList<Product>) {
        val cartRecyclerViewAdapter = RecyclerViewAdapter(this, cartList, productAmountsHashMap)
        cartProductsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        cartProductsRecyclerView.adapter = cartRecyclerViewAdapter

    }

    fun updatePayAmount(productPrice: Double?) {
        wholePayAmount += productPrice!!
        totalPayAmountTextView.text = roundOffDecimal(wholePayAmount).toString()
    }

}