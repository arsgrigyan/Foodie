package com.southernsunrise.foodie.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.firebase.FirestoreClass
import com.southernsunrise.foodie.fragments.CartFragment
import com.southernsunrise.foodie.models.Product
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(
    private val fragment: Fragment,
    private val productList: ArrayList<Product>,
    private val productAmountsHashMap: HashMap<Long, Any>
) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        val product = productList[position]
        var productAmount: Int = productAmountsHashMap[product.productId] as Int
        Picasso.get().load(product.productImage).into(holder.cartProductImageView)
        holder.cartProductNameTextView.text = product.productName
        holder.cartProductPriceTextView.text = product.productPrice.toString()
        holder.productAmountTextView.text = productAmount.toString()

        if (fragment is CartFragment) {

            holder.increaseProductAmountButton.setOnClickListener {
                    holder.productAmountTextView.text = (++productAmount).toString()
                    fragment.updatePayAmount(product.productPrice)
                FirestoreClass().updateProductAmountInCart(fragment, product, productAmount)
            }

            holder.decreaseProductAmountButton.setOnClickListener {
                if (productAmount > 1) {
                    holder.productAmountTextView.text = (--productAmount).toString()
                    fragment.updatePayAmount(-(product.productPrice)!!)
                    FirestoreClass().updateProductAmountInCart(fragment, product, productAmount)
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val cartProductImageView: ImageView = ItemView.findViewById(R.id.cart_product_imageView)
        val cartProductNameTextView: TextView = ItemView.findViewById(R.id.tv_cart_product_name)
        val cartProductPriceTextView: TextView = ItemView.findViewById(R.id.cart_product_price)
        val productAmountTextView: TextView = ItemView.findViewById(R.id.tv_product_amount)
        val decreaseProductAmountButton: ImageButton =
            ItemView.findViewById(R.id.btn_decrease_amount)
        val increaseProductAmountButton: ImageButton =
            ItemView.findViewById(R.id.btn_increase_amount)

    }
}