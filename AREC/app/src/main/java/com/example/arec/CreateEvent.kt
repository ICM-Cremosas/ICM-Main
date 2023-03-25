package com.example.arec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.arec.databinding.CreateEventBinding

class CreateEvent : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<CreateEventBinding>(inflater, R.layout.create_event,container,false)
        return binding.root
    }
}