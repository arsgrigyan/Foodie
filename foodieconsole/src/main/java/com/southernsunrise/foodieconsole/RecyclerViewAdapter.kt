package com.southernsunrise.foodieconsole

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.southernsunrise.foodieconsole.utilities.Product
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(private val activity: MainActivity, private val productList: ArrayList<Product>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gridview_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = productList[position]

        // sets the image to the imageview from our itemHolder class
        Picasso.get().load(product.productImage.toString()).into(holder.productImageView)


        holder.productNameTextView.text = product.productName
        holder.productPriceTextView.text = product.productPrice.toString()
        holder.deleteProductButton.setOnClickListener {
            activity.deleteProduct(product)
        }
        holder.editProductButton.setOnClickListener {
            activity.showDialogForEditingProduct(product)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return productList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.product_image)
        val productNameTextView: TextView = itemView.findViewById(R.id.product_name)
        val productPriceTextView: TextView = itemView.findViewById(R.id.tv_price)
        val deleteProductButton:ImageButton = itemView.findViewById(R.id.delete_product)
        val editProductButton:ImageButton = itemView.findViewById(R.id.edit_product)

    }
}




