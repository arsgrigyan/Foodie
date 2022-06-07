package com.southernsunrise.foodie.fragments

import android.os.Build
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.utilities.Constants


class OrderFragment(private val checkoutAmount: Double) : Fragment(), View.OnClickListener {

    private lateinit var backButton: ImageButton
    private lateinit var placeOrderButton: Button
    private lateinit var homeAddressCheckBox: CheckBox
    private lateinit var officeAddressCheckBox: CheckBox
    private lateinit var paymentCreditCheckBox: CheckBox
    private lateinit var paymentCashCheckBox: CheckBox
    private lateinit var cashOnDeliveryPayAmountTextView: TextView
    private lateinit var totalPayAmountTextView: TextView
    private lateinit var payOnCreditEditText: EditText
    private lateinit var homeAddressEditText: EditText
    private lateinit var officeAddressEditText: EditText
    private lateinit var paymentCardImageView: ImageView

    private lateinit var textWatcher: TextWatcher

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        backButton = view.findViewById(R.id.ib_back_arrow)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        homeAddressCheckBox = view.findViewById(R.id.cb_home_address)
        homeAddressCheckBox.setOnClickListener(this)
        officeAddressCheckBox = view.findViewById(R.id.cb_office_address)
        officeAddressCheckBox.setOnClickListener(this)
        paymentCreditCheckBox = view.findViewById(R.id.cb_payment_credit)
        paymentCreditCheckBox.setOnClickListener(this)
        paymentCashCheckBox = view.findViewById(R.id.cb_payment_cash)
        paymentCashCheckBox.setOnClickListener(this)
        placeOrderButton = view.findViewById(R.id.btn_place_order)
        placeOrderButton.setOnClickListener(this)
        cashOnDeliveryPayAmountTextView = view.findViewById(R.id.tv_cashOnDelivery_pay_amount)
        cashOnDeliveryPayAmountTextView.text = "$$checkoutAmount"
        totalPayAmountTextView = view.findViewById(R.id.tv_total_pay_amount)
        totalPayAmountTextView.text = "$$checkoutAmount"
        payOnCreditEditText = view.findViewById(R.id.et_pay_on_credit)
        payOnCreditEditText.setOnFocusChanged()
        homeAddressEditText = view.findViewById(R.id.et_address_home)
        homeAddressEditText.setOnFocusChanged()
        officeAddressEditText = view.findViewById(R.id.et_address_office)
        officeAddressEditText.setOnFocusChanged()

        paymentCardImageView = view.findViewById(R.id.iv_card)

        getAddressesAndSetupEditTexts()
        getCheckedAddressAndPaymentMethodAndSetupCheckBoxes()
        getCreditCardInfo()

        return view
    }


    fun getCheckedAddressAndPaymentMethodAndSetupCheckBoxes() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid).get()
            .addOnSuccessListener {
                val checkedAddressType = it.getString(Constants.CHECKED_ADDRESS_TYPE)
                val checkedPaymentType = it.getString(Constants.CHECKED_PAYMENT_TYPE)

                when (checkedAddressType) {
                    Constants.CHECKED_ADDRESS_TYPE_HOME -> {
                        homeAddressCheckBox.isChecked = true

                    }
                    Constants.CHECKED_ADDRESS_TYPE_OFFICE -> {
                        officeAddressCheckBox.isChecked = true
                    }
                }
                when (checkedPaymentType) {

                    Constants.CHECKED_PAYMENT_TYPE_CASH -> {
                        paymentCashCheckBox.isChecked = true

                    }
                    Constants.CHECKED_PAYMENT_TYPE_CASH_FREE -> {
                        paymentCreditCheckBox.isChecked = true

                    }

                }
            }
    }


    fun getAddressesAndSetupEditTexts() {
        val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid).get()
            .addOnSuccessListener {
                val homeAddress = it.getString(Constants.ADDRESS_HOME)
                val officeAddress = it.getString(Constants.ADDRESS_OFFICE)
                if (!homeAddress.isNullOrBlank()) {
                    homeAddressEditText.setText(homeAddress)
                }
                if (!officeAddress.isNullOrBlank()) {
                    officeAddressEditText.setText(officeAddress)

                }
            }

    }

    fun getCreditCardInfo() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUser.uid).get()
            .addOnSuccessListener {
                val creditCardCode = it.getLong(Constants.CREDIT_CARD) ?: ""
                val cashFreePaymentMethod = it.getString(Constants.CHECKED_CASH_FREE_PAYMENT_TYPE)
                when (cashFreePaymentMethod) {
                    Constants.PAYMENT_METHOD_MASTERCARD -> {
                        paymentCardImageView.setImageResource(R.drawable.ic_mastercard)
                    }
                    Constants.PAYMENT_METHOD_VISA -> {
                        paymentCardImageView.setImageResource(R.drawable.ic_visa)

                    }
                    Constants.PAYMENT_METHOD_PAYPAL -> {

                        // changing card payment edittext to paypal editText by changing some design
                        paymentCardImageView.setImageResource(R.drawable.ic_paypal)
                        payOnCreditEditText.setText("$$checkoutAmount")
                        payOnCreditEditText.transformationMethod = null;
                        payOnCreditEditText.isFocusable = false
                        val params: ViewGroup.MarginLayoutParams = payOnCreditEditText.layoutParams as ViewGroup.MarginLayoutParams
                        params.leftMargin = 4
                        payOnCreditEditText.layoutParams = params
                    }

                }
                if (cashFreePaymentMethod != Constants.PAYMENT_METHOD_PAYPAL) {
                    if (creditCardCode.toString().isNotBlank()) {
                        payOnCreditEditText.setText(creditCardCode.toString())
                    }
                }

            }
    }


    override fun onClick(p0: View?) {
        if (p0 is CheckBox) {
            when (p0.id) {
                R.id.cb_home_address -> {
                    p0.isChecked = true
                    FirestoreClass().setOrUpdateCheckedAddress(Constants.CHECKED_ADDRESS_TYPE_HOME)
                    officeAddressCheckBox.isChecked = false

                }
                R.id.cb_office_address -> {
                    p0.isChecked = true
                    FirestoreClass().setOrUpdateCheckedAddress(Constants.CHECKED_ADDRESS_TYPE_OFFICE)
                    homeAddressCheckBox.isChecked = false
                }
                R.id.cb_payment_credit -> {
                    p0.isChecked = true
                    paymentCashCheckBox.isChecked = false
                    FirestoreClass().setOrUpdateCheckedPaymentMethod(Constants.CHECKED_PAYMENT_TYPE_CASH_FREE)

                }
                R.id.cb_payment_cash -> {
                    p0.isChecked = true
                    paymentCreditCheckBox.isChecked = false
                    FirestoreClass().setOrUpdateCheckedPaymentMethod(Constants.CHECKED_PAYMENT_TYPE_CASH)

                }

            }

        } else {
            when (p0?.id) {
                R.id.btn_place_order -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.order_unavailable),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    fun EditText.setOnFocusChanged() {
        this.setOnFocusChangeListener { view, b ->
            if (!view.isFocused) {
                when (view.id) {
                    R.id.et_address_home -> {
                        FirestoreClass().setOrUpdateUserAddresses(
                            this.text.toString(),
                            Constants.ADDRESS_HOME
                        )
                    }
                    R.id.et_address_office -> {
                        FirestoreClass().setOrUpdateUserAddresses(
                            this.text.toString(),
                            Constants.ADDRESS_OFFICE
                        )

                    }
                    R.id.et_pay_on_credit -> {
                        if (this.text.length == 16) {
                            FirestoreClass().setOrUpdateCreditCardInfo(
                                this.text.toString().toLong()
                            )
                        }

                    }
                }
            }
        }
    }


}

