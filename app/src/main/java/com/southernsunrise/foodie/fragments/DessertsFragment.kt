package com.southernsunrise.foodie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.adapters.ProductListAdapter
import com.southernsunrise.foodie.models.Product
import com.southernsunrise.foodie.utilities.GetProducts

class DessertsFragment : Fragment() {

    private lateinit var dessertsGridView: GridView
    private lateinit var errorMessageView: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tryAgainTextView: TextView
    private var dessertsList: ArrayList<Product>? = ArrayList()

    companion object {
        val bundle: Bundle = Bundle()
    }

    private var error: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_desserts, container, false)

        dessertsGridView = view.findViewById(R.id.desserts_gridView)
        progressBar = view.findViewById(R.id.progressBar)
        errorMessageView = view.findViewById(R.id.error_message)
        tryAgainTextView = view.findViewById<TextView>(R.id.tv_tryAgain)


        getDessertsList()


        return view
    }

    private fun getDessertsList() {
        GetProducts(this).getProducts()
    }

    fun setDesserts(dessertsList: ArrayList<Product>) {
        if (dessertsList.isNotEmpty()) {
            val adapter = ProductListAdapter(requireContext(), dessertsList)
            dessertsGridView.adapter = adapter
            this.dessertsList = dessertsList

            dessertsGridView.setOnItemClickListener { adapterView, view, i, l ->
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ProductDetailsFragment(dessertsList[i]))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit()
            }
        }

    }


    fun progressbarVisible(visible: Boolean) {
        if (visible) {
            progressBar.visibility = View.VISIBLE
        } else progressBar.visibility = View.GONE
    }


}


/* if (!bundle.isEmpty) {
       dessertsList = bundle.getSerializable("dessertsList") as? ArrayList<ProductListItem>
       dessertsData.setList(dessertsGridView, dessertsList!!, progressBar)
       val gridPos = bundle.getInt("dessertsPos")
       dessertsGridView.setSelection(gridPos)

   } else dessertsData.getProductList().execute()

   */


/* setTryAgainText()

     dessertsData = GetProductData(
         dessertsGridView,
         progressBar,
         errorMessageView,
         requireActivity().applicationContext,
         requireActivity().supportFragmentManager,
         "dessert"

     )


     */


/* private fun setTryAgainText() {
   val ss = SpannableString(tryAgainTextView.text.toString())
   ss[0..ss.length] = object : ClickableSpan() {
       override fun onClick(view: View) {
           errorMessageView.visibility = View.GONE
           getDessertsList()
       }

       override fun updateDrawState(ds: TextPaint) {
           super.updateDrawState(ds)
           ds.color = Color.parseColor("#8EA545")
           ds.isUnderlineText = false
       }
   }
   tryAgainTextView.text = ss
   tryAgainTextView.movementMethod = LinkMovementMethod()
}

*/

