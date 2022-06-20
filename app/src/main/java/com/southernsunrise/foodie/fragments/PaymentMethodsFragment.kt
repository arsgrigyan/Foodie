package com.southernsunrise.foodie.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toolbar
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.utilities.Constants
import com.southernsunrise.foodie.utilities.Constants.PAYMENT_METHOD_MASTERCARD
import com.southernsunrise.foodie.utilities.Constants.PAYMENT_METHOD_VISA


class PaymentMethodsFragment : Fragment(), View.OnClickListener {

    private lateinit var radioButtonMastercard: RadioButton
    private lateinit var radioButtonPaypal: RadioButton
    private lateinit var radioButtonVisa: RadioButton

    private lateinit var layoutRadioButtonMastercard: LinearLayout
    private lateinit var layoutRadioButtonPaypal: LinearLayout
    private lateinit var layoutRadioButtonVisa: LinearLayout

    private lateinit var doneButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_payment_methods, container, false)
        val window: Window = requireActivity().window
        window.statusBarColor = Color.parseColor("#fafafa")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        activity?.setActionBar(toolbar)
        activity?.actionBar?.title = null
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        radioButtonMastercard = view.findViewById(R.id.rb_mastercard)
        radioButtonPaypal = view.findViewById(R.id.rb_paypal)
        radioButtonVisa = view.findViewById(R.id.rb_visa)

        layoutRadioButtonMastercard = view.findViewById(R.id.layout_rb_mastercard)
        layoutRadioButtonPaypal = view.findViewById(R.id.layout_rb_paypal)
        layoutRadioButtonVisa = view.findViewById(R.id.layout_rb_visa)

        layoutRadioButtonVisa.setOnClickListener(this)
        layoutRadioButtonPaypal.setOnClickListener(this)
        layoutRadioButtonMastercard.setOnClickListener(this)
        doneButton = view.findViewById(R.id.btn_done)
        doneButton.setOnClickListener(this)
        getCheckedPaymentMethod()
        return view
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.layout_rb_mastercard -> {
                checkPaymentMethodMastercard()
            }

            R.id.layout_rb_paypal -> {
                checkPaymentMethodPaypal()
            }

            R.id.layout_rb_visa -> {
                checkPaymentMethodVisa()

            }
            R.id.btn_done -> {
                requireActivity().onBackPressed()
            }

        }
    }

    fun checkPaymentMethodMastercard() {
        radioButtonMastercard.isChecked = true
        radioButtonVisa.isChecked = false
        radioButtonPaypal.isChecked = false
        FirestoreClass().setOrUpdateCheckedCashFreePaymentType(PAYMENT_METHOD_MASTERCARD)
    }

    fun checkPaymentMethodPaypal() {
        radioButtonMastercard.isChecked = false
        radioButtonVisa.isChecked = false
        radioButtonPaypal.isChecked = true
        FirestoreClass().setOrUpdateCheckedCashFreePaymentType(Constants.PAYMENT_METHOD_PAYPAL)
    }
    fun checkPaymentMethodVisa() {
        radioButtonMastercard.isChecked = false
        radioButtonVisa.isChecked = true
        radioButtonPaypal.isChecked = false
        FirestoreClass().setOrUpdateCheckedCashFreePaymentType(PAYMENT_METHOD_VISA)
    }

    fun getCheckedPaymentMethod() {
        val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid)
            .get()
            .addOnSuccessListener {
                val savedPaymentMethod = it.getString(Constants.CHECKED_CASH_FREE_PAYMENT_TYPE)

                when (savedPaymentMethod) {

                    Constants.PAYMENT_METHOD_MASTERCARD -> {
                        radioButtonMastercard.isChecked = true
                    }

                    Constants.PAYMENT_METHOD_VISA -> {
                        radioButtonVisa.isChecked = true
                    }

                    Constants.PAYMENT_METHOD_PAYPAL -> {
                        radioButtonPaypal.isChecked = true
                    }
                }
            }
    }


}
