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


class BeveragesFragment : Fragment() {

    private lateinit var beveragesGridView: GridView
    private lateinit var errorMessageView: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tryAgainTextView: TextView
    var beveragesList: ArrayList<Product>? = null

    companion object {
        var bundle: Bundle = Bundle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_beverages, container, false)

        beveragesGridView = view.findViewById(R.id.beverages_gridView)
        errorMessageView = view.findViewById(R.id.error_message)
        progressBar = view.findViewById(R.id.progressBar)
        tryAgainTextView = view.findViewById<TextView>(R.id.tv_tryAgain)

        getBeveragesList()
        return view
    }

    private fun getBeveragesList() {
        GetProducts(this).getProducts()

    }

    fun setBeverages(beveragesList: ArrayList<Product>) {

        if (beveragesList.isNotEmpty()) {
            val adapter = ProductListAdapter(requireContext(), beveragesList)
            beveragesGridView.adapter = adapter
            this.beveragesList = beveragesList

            beveragesGridView.setOnItemClickListener { adapterView, view, i, l ->
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ProductDetailsFragment(beveragesList[i]))
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
















/*   setTryAgainText()

      val fragmentManager = requireActivity().supportFragmentManager
      beveragesData = GetProductData(
          beveragesGridView,
          progressBar,
          errorMessageView,
          requireActivity().applicationContext,
          fragmentManager,
          "drinks"
      )

    */


/*  if (!bundle.isEmpty) {
             beveragesList = bundle.getSerializable("beveragesList") as? ArrayList<ListItem>?
             beveragesData.setList(beveragesGridView, beveragesList!!, progressBar)
             val gridViewPos = bundle.getInt("beveragesListPos", 0)
             beveragesGridView.setSelection(gridViewPos)
         } else beveragesData.getProductList().execute()

        */


/*
    private fun setTryAgainText() {
        val ss = SpannableString(tryAgainTextView.text.toString())
        ss[0..ss.length] = object : ClickableSpan() {
            override fun onClick(view: View) {
                errorMessageView.visibility = View.GONE
                setBeveragesList()
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