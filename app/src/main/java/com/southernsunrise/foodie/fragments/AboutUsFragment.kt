package com.southernsunrise.foodie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment


class AboutUsFragment : Fragment(), View.OnClickListener {

    private lateinit var backArrowImageButton: ImageButton
    private lateinit var linkedinIconImageButton: ImageButton
    private lateinit var githubIconImageButton: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)
        backArrowImageButton = view.findViewById(R.id.ib_back_arrow)
        backArrowImageButton.setOnClickListener { requireActivity().onBackPressed() }

        linkedinIconImageButton = view.findViewById(R.id.ib_linkedin)
        githubIconImageButton = view.findViewById(R.id.ib_github)
        linkedinIconImageButton.setOnClickListener(this)
        githubIconImageButton.setOnClickListener(this)

        return view
    }

    override fun onClick(p0: View?) {
        var link: String = ""
        when (p0?.id) {
            R.id.ib_github -> {
                link = "https://github.com/arsgrigyan"

            }

            R.id.ib_linkedin -> {
                link = "https://www.linkedin.com/in/arsen-grigoryan-bba159236/"
            }
        }
        val uri: Uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}
