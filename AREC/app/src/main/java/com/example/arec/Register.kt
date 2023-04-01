package com.example.arec

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.arec.databinding.RegisterBinding

class Register : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<RegisterBinding>(inflater, R.layout.register,container,false)

        binding.butRegister.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_register_to_editProfileOnRegister) }
        return binding.root
    }


}