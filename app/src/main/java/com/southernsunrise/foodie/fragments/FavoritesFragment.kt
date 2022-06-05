package com.southernsunrise.foodie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.adapters.ProductListAdapter
import com.southernsunrise.foodie.models.Product
import com.southernsunrise.foodie.utilities.GetProducts


class FavoritesFragment : Fragment() {

    private var favoritesList: ArrayList<Product>? = null
    private lateinit var favoritesGridView: GridView
private lateinit var noFavoritesTextView:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        favoritesGridView = view.findViewById(R.id.fav_gridView)
        noFavoritesTextView = view.findViewById(R.id.tv_noFavorites)
        getFavoriteProducts()

        return view
    }

    private fun getFavoriteProducts() {
        GetProducts(this).getFavoritesList(this)
    }

    fun setFavoriteProducts(favoriteProductsList: ArrayList<Product>) {

        if (favoriteProductsList.isNotEmpty()) {
            val adapter = ProductListAdapter(requireContext(), favoriteProductsList)
            favoritesGridView.adapter = adapter
            favoritesList = favoriteProductsList

        }
    }

   /* private fun navigateToDetailsFragment(bundle: Bundle) {
        val nextFrag = ProductDetailsFragment()
        nextFrag.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, nextFrag)
            .addToBackStack(null)
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .commit()
    }


    */
    fun noFavorites() {
        noFavoritesTextView.visibility = View.VISIBLE
    }


}