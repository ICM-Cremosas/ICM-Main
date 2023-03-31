package com.example.arec

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.pink)))

        val actionBarLayout = layoutInflater.inflate(R.layout.action_bar_layout, null)
        supportActionBar?.setCustomView(actionBarLayout)
        supportActionBar?.setDisplayShowCustomEnabled(true)

        actionBarLayout.findViewById<ImageView>(R.id.iv_map).setOnClickListener {
            // Handle map icon click
        }

        actionBarLayout.findViewById<ImageView>(R.id.iv_chat).setOnClickListener {
            // Handle chat icon click
        }

        actionBarLayout.findViewById<ImageView>(R.id.iv_profile).setOnClickListener {
            // Handle profile icon click
        }
    }



}