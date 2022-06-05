package com.southernsunrise.foodie.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.southernsunrise.foodie.R
import com.southernsunrise.foodie.models.ProfileListItem

class ProfileListAdapter(var context: Context, var itemList: ArrayList<ProfileListItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(p0: Int): Any {
        return itemList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.profile_listitem, null)

        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val title: TextView = view.findViewById(R.id.itemTitle)
        val listItem = itemList[p0]
        title.text = listItem.itemTitle.toString()
        icon.setImageResource(listItem.itemIcon)


        return view
    }
}