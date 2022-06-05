package com.southernsunrise.foodieconsole

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.foodieconsole.utilities.Constants
import com.southernsunrise.foodieconsole.utilities.Product
import com.squareup.picasso.Picasso
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productCaloriesEditText: EditText
    private lateinit var readyInMinutesEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var productImageView: ImageView
    private lateinit var productPhotoImageViewGreyLayer: RelativeLayout


    private lateinit var addFoodButton: ImageButton
    private lateinit var addDrinkButton: ImageButton
    private lateinit var addDessertButton: ImageButton


    private lateinit var foodRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var beveragesRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var dessertsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var productType: String


    private lateinit var productName: String
    private lateinit var productDescription: String
    var readyInMinutes: Double = 0.0
    private var productPrice = 0.0
    private var productCalories = 0.0
    private var productImageUri: Uri? = null
    var productId: Long = 0

    private lateinit var textWatcher: TextWatcher

    private var addProductButton: Button? = null
    private var updateProductDataButton: Button? = null
    private var imageUpdated: Boolean = false
    private lateinit var productImageToBeUpdated: Any

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foodRecyclerView = findViewById(R.id.food_recyclerView)
        beveragesRecyclerView = findViewById(R.id.beverages_recyclerView)
        dessertsRecyclerView = findViewById(R.id.desserts_recyclerView)


        getAndSetProducts(foodRecyclerView)
        getAndSetProducts(beveragesRecyclerView)
        getAndSetProducts(dessertsRecyclerView)


        addFoodButton = findViewById(R.id.btn_add_food)
        addDrinkButton = findViewById(R.id.btn_add_drink)
        addDessertButton = findViewById(R.id.btn_add_dessert)
        addFoodButton.setOnClickListener(this)
        addDrinkButton.setOnClickListener(this)
        addDessertButton.setOnClickListener(this)


    }

    private fun setupTextWatcher(button: Button) {
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (button == addProductButton) {
                    button.isEnabled = allDataInputted()
                } else if (button == updateProductDataButton) {
                    button.isEnabled = allFieldsFilled()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showDialogForAddingProduct(productType: String) {
        this.productType = productType
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.WrapContentDialog)
        alertDialogBuilder.setView(R.layout.dialog_bg)
        alertDialogBuilder.setPositiveButton(
            "add",
            DialogInterface.OnClickListener { dialogInterface, i ->
                if (allDataInputted()) {
                    addProductToDB()

                } else Toast.makeText(this, "Please, fill all the fields", Toast.LENGTH_SHORT)
                    .show()
            })

        val dialog = alertDialogBuilder.create()
        dialog.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog_background);

        dialog.show()
        addProductButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        updateProductDataButton = null
        addProductButton!!.isEnabled = false

        setupTextWatcher(addProductButton!!)

        productNameEditText = dialog.window!!.findViewById(R.id.et_product_name)
        productPriceEditText = dialog.window!!.findViewById(R.id.et_product_price)
        productCaloriesEditText = dialog.window!!.findViewById(R.id.et_product_calories)
        readyInMinutesEditText = dialog.window!!.findViewById(R.id.et_ready_in_minutes)
        descriptionEditText = dialog.window!!.findViewById(R.id.et_product_description)

        productNameEditText.addTextChangedListener(textWatcher)
        productPriceEditText.addTextChangedListener(textWatcher)
        productCaloriesEditText.addTextChangedListener(textWatcher)
        readyInMinutesEditText.addTextChangedListener(textWatcher)
        descriptionEditText.addTextChangedListener(textWatcher)

        productImageView = dialog.window!!.findViewById(R.id.iv_product_image)
        productPhotoImageViewGreyLayer =
            dialog.window!!.findViewById(R.id.product_imageView_grey_layer)

        productImageView.setOnClickListener {
            showImageChooserOrAskForPermission()
        }

        dialog.setOnDismissListener {
            removePreviousImageUri()
        }


    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showDialogForEditingProduct(product: Product) {
        this.productType = product.productType!!
        this.productId = product.productId!!

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.WrapContentDialog)
        alertDialogBuilder.setView(R.layout.dialog_bg)
        productImageToBeUpdated = product.productImage


        alertDialogBuilder.setPositiveButton(
            "Save",
            DialogInterface.OnClickListener { dialogInterface, i ->

                if (allFieldsFilled()) {

                    val productInfo = hashMapOf<String, Any>(
                        Constants.PRODUCT_NAME to productName,
                        Constants.PRODUCT_PRICE to productPrice,
                        Constants.PRODUCT_TYPE to productType,
                        Constants.PRODUCT_ID to productId,
                        Constants.PRODUCT_CALORIES to productCalories,
                        Constants.READY_IN_MINUTES to readyInMinutes,
                        Constants.PRODUCT_DESCRIPTION to productDescription,
                        Constants.PRODUCT_IMAGE to productImageToBeUpdated

                    )
                    updateProductDetails(productInfo)
                   Toast.makeText(this, productImageToBeUpdated.toString(), Toast.LENGTH_SHORT).show()
                }
            })

        val dialog = alertDialogBuilder.create()
        dialog.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show()

        updateProductDataButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        addProductButton = null
        updateProductDataButton!!.isEnabled = false

        setupTextWatcher(updateProductDataButton!!)



        productNameEditText = dialog.window!!.findViewById(R.id.et_product_name)
        productPriceEditText = dialog.window!!.findViewById(R.id.et_product_price)
        productCaloriesEditText = dialog.window!!.findViewById(R.id.et_product_calories)
        readyInMinutesEditText = dialog.window!!.findViewById(R.id.et_ready_in_minutes)
        descriptionEditText = dialog.window!!.findViewById(R.id.et_product_description)
        productImageView = dialog.window!!.findViewById(R.id.iv_product_image)
        productPhotoImageViewGreyLayer =
            dialog.window!!.findViewById(R.id.product_imageView_grey_layer)

        productNameEditText.setText(product.productName)
        productPriceEditText.setText(product.productPrice.toString())
        productCaloriesEditText.setText(product.productCalories.toString())
        readyInMinutesEditText.setText(product.readyInMinutes.toString())
        descriptionEditText.setText(product.productDescription)

        Picasso.get().load(product.productImage.toString()).into(productImageView)
        productImageView.setOnClickListener {
            showImageChooserOrAskForPermission()
        }

        productNameEditText.addTextChangedListener(textWatcher)
        productPriceEditText.addTextChangedListener(textWatcher)
        productCaloriesEditText.addTextChangedListener(textWatcher)
        readyInMinutesEditText.addTextChangedListener(textWatcher)
        descriptionEditText.addTextChangedListener(textWatcher)

        dialog.setOnDismissListener {
            removePreviousImageUri()
        }
    }

    private fun updateProductDetails(productInfo: HashMap<String, Any>) {
        // if productInfo[Constants.PRODUCT_IMAGE] is Uri? then theres a new photo selected to update the product
        if(productInfo[Constants.PRODUCT_IMAGE] is Uri?) {
            FirestoreClass().uploadNewImageAndUpdateProductDetails(this, productInfo)
        } else FirestoreClass().updateProductDetails(this, productInfo)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_add_food -> {
                showDialogForAddingProduct(Constants.FOOD)

            }
            R.id.btn_add_drink -> {
                showDialogForAddingProduct(Constants.BEVERAGES)

            }
            R.id.btn_add_dessert -> {
                showDialogForAddingProduct(Constants.DESSERTS)

            }
        }
    }

    private fun addProductToDB() {
        val productID = System.currentTimeMillis()
        val product = Product(
            productName,
            productPrice,
            productImageUri!!,
            productType,
            productID,
            productCalories,
            readyInMinutes,
            productDescription
        )
        FirestoreClass().addProductToDataBase(this, product)
        //  productImageUri = null
    }


    fun allDataInputted(): Boolean {
        productName = productNameEditText.text.toString().trim()
        val productPriceText = productPriceEditText.text.toString()
        if (productPriceText.isNotBlank()) {
            productPrice = productPriceText.toString().toDouble()
        }
        val productCaloriesString = productCaloriesEditText.text.toString()
        if (productCaloriesString.isNotBlank()) {
            productCalories = productCaloriesString.toDouble()
        }
        val readyInMinutesString = readyInMinutesEditText.text.toString()
        if (readyInMinutesString.isNotBlank()) {
            readyInMinutes = readyInMinutesString.toDouble()
        }
        productDescription = descriptionEditText.text.toString()

        return productName.isNotBlank() && productPriceText.isNotBlank() && productType.isNotBlank() &&
                productCaloriesString.isNotBlank() && readyInMinutesString.isNotBlank() && productDescription.isNotBlank() && productImageUri != null
    }

    fun allFieldsFilled(): Boolean {
        val productPriceText = productPriceEditText.text.toString()
        if (productPriceText.isNotBlank()) {
            productPrice = productPriceText.toString().toDouble()
        }
        val productCaloriesString = productCaloriesEditText.text.toString()
        if (productCaloriesString.isNotBlank()) {
            productCalories = productCaloriesString.toDouble()
        }
        val readyInMinutesString = readyInMinutesEditText.text.toString()
        if (readyInMinutesString.isNotBlank()) {
            readyInMinutes = readyInMinutesString.toDouble()
        }
        productDescription = descriptionEditText.text.toString()


        productName = productNameEditText.text.toString()

        return productName.isNotBlank() && productPriceText.isNotBlank() && productType.isNotBlank() &&
                productCaloriesString.isNotBlank() && readyInMinutesString.isNotBlank() && productDescription.isNotBlank()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showImageChooserOrAskForPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Constants.showImageChooser(this)
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                Constants.showImageChooser(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        productImageUri = data.data!!
                        if (allFieldsFilled()) {
                            enableButtons()
                        }
                        productPhotoImageViewGreyLayer.visibility = View.GONE
                        productImageView.setImageURI(productImageUri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }


                }

            }
        }


    }

    private fun enableButtons() {
        if (addProductButton != null) {
            addProductButton!!.isEnabled = true
        } else if (updateProductDataButton != null) {
            // giving productImageUri's value to productImageToBeUpdated to upload it and update the product
            productImageToBeUpdated = productImageUri!!
            updateProductDataButton!!.isEnabled = true
        }
    }


    private fun getAndSetProducts(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        var productType = ""
        when (recyclerView.id) {
            R.id.food_recyclerView -> {
                productType = Constants.FOOD
            }
            R.id.beverages_recyclerView -> {
                productType = Constants.BEVERAGES
            }
            R.id.desserts_recyclerView -> {
                productType = Constants.DESSERTS
            }


        }

        FirebaseFirestore.getInstance().collection(Constants.PRODUCTS).document(productType)
            .collection(Constants.BASE).get().addOnSuccessListener {
                val productsDocumentList: MutableList<DocumentSnapshot> = it.documents
                val productsList: ArrayList<Product> = ArrayList()
                for (product in productsDocumentList) {
                    productsList.add(
                        Product(
                            product.getString(Constants.PRODUCT_NAME).toString(),
                            product.getDouble(Constants.PRODUCT_PRICE)!!,
                            product.getString(Constants.PRODUCT_IMAGE).toString(),
                            product.getString(Constants.PRODUCT_TYPE),
                            product.getLong(Constants.PRODUCT_ID)!!.toLong(),
                            product.getDouble(Constants.PRODUCT_CALORIES),
                            product.getDouble(Constants.READY_IN_MINUTES),
                            product.getString(Constants.PRODUCT_DESCRIPTION).toString()
                        )
                    )
                }
                if (productsList.isNotEmpty()) {
                    val layoutManager: GridLayoutManager =
                        GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
                    val recyclerViewAdapter: RecyclerViewAdapter =
                        RecyclerViewAdapter(this, productsList)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.adapter = recyclerViewAdapter


                }
            }
    }


    fun deleteProduct(product: Product) {
        FirestoreClass().removeProductFromDB(this, product)
        val productType = product.productType!!
        updateRecyclerViews(productType)
    }

    fun updateRecyclerViews(productType: String) {
        var recyclerViewToUpdate: RecyclerView? = null
        when (productType) {
            Constants.FOOD -> {
                recyclerViewToUpdate = foodRecyclerView
            }
            Constants.BEVERAGES -> {
                recyclerViewToUpdate = beveragesRecyclerView
            }
            Constants.DESSERTS -> {
                recyclerViewToUpdate = dessertsRecyclerView
            }
        }
        getAndSetProducts(recyclerViewToUpdate!!)
    }

    fun removePreviousImageUri() {
        productImageUri = null
    }

}