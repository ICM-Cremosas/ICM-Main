package com.example.arec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.arec.databinding.AskLocBinding

class AskLoc : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<AskLocBinding>(inflater, R.layout.ask_loc,container,false)
        return binding.root
    }

}