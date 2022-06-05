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
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.adapters.ProductListAdapter
import com.southernsunrise.foodie.models.Product
import com.southernsunrise.foodie.utilities.GetProducts


class FoodFragment : Fragment() {

    private lateinit var foodGridView: GridView
    private lateinit var errorMessageView: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tryAgainTextView: TextView
    var foodList: ArrayList<Product>? = null


    companion object {
        var bundle: Bundle = Bundle()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_food, container, false)
        foodGridView = view.findViewById(R.id.food_gridView)
        errorMessageView = view.findViewById(R.id.error_message)
        progressBar = view.findViewById(R.id.progressBar)

        tryAgainTextView = view.findViewById<TextView>(R.id.tv_tryAgain)


        getFoodList()


        return view
    }

    private fun getFoodList() {
        GetProducts(this).getProducts()
    }

    fun setFood(foodList: ArrayList<Product>) {
        if (foodList.isNotEmpty()) {
            val adapter = ProductListAdapter(requireContext(), foodList)
            foodGridView.adapter = adapter
            this.foodList = foodList
        }
        foodGridView.setOnItemClickListener { adapterView, view, i, l ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ProductDetailsFragment(foodList[i]))
                .setTransition(TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit()
        }
    }

    fun progressbarVisible(visible: Boolean) {
        if (visible) {
            progressBar.visibility = View.VISIBLE
        } else progressBar.visibility = View.GONE
    }


}


/*  setTryAgainText()

    val fragmentManager = requireActivity().supportFragmentManager
    getFoodData = GetProductData(
        foodGridView,
        progressBar,
        errorMessageView,
        requireActivity().applicationContext,
        fragmentManager,
        "meal"

    )

   */

/*  private fun setTryAgainText(){
  val ss = SpannableString(tryAgainTextView.text.toString())
  ss[0..ss.length] = object: ClickableSpan(){
      override fun onClick(view: View) {
          errorMessageView.visibility = View.GONE
          getFoodList()
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


/*if (!bundle.isEmpty) {
        foodList = bundle.getSerializable("foodList") as? ArrayList<ProductListItem>?
        getFoodData.setList(foodGridView, foodList!!, progressBar)
        val gridViewPos = bundle.getInt("foodListPos", 0)
        foodGridView.setSelection(gridViewPos)

    } else getFoodData.getProductList().execute()

     */




