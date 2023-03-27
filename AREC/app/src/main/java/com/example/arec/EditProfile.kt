package com.example.arec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.arec.databinding.EditProfileBinding

class EditProfile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<EditProfileBinding>(inflater, R.layout.edit_profile,container,false)

        binding.butSave.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_editProfile_to_askLoc) }
        return binding.root
    }
}