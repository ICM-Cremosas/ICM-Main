package com.example.arec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.arec.databinding.LoginBinding

class Login : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<LoginBinding>(inflater, R.layout.login,container,false)

        // hide the action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.butLogin.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_login_to_mapsFragment) }

        binding.textDontHaveAccount.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_login_to_register) }

        return binding.root
    }
}