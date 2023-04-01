package com.example.arec

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arec.databinding.EditProfileOnRegisterBinding
import com.example.arec.databinding.EditProfileBinding

class EditProfileOnRegister : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<EditProfileOnRegisterBinding>(inflater, R.layout.edit_profile_on_register,container,false)

        binding.butSaveRegister.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_editProfileOnRegister_to_askLoc) }
        return binding.root
    }

}