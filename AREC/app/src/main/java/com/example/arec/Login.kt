package com.example.arec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.arec.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : Fragment() {

    private lateinit var auth: FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<LoginBinding>(inflater, R.layout.login,container,false)

        // hide the action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        if (auth!!.currentUser != null) {
            findNavController().navigate(R.id.action_login_to_maps)
        }

        binding.btnSignup.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_login_to_register)
        }

        binding.btnLogin.setOnClickListener { view : View ->
            //code for loggin in
            val bundleLoginOtp = Bundle()
            val phone = binding.edtPhone.text.toString()
            if(!phone.equals("")) {
                bundleLoginOtp.putString("phone", phone)
                view.findNavController().navigate(R.id.action_login_to_OTP, bundleLoginOtp)
            }
            else{
                Toast.makeText(requireContext(), "Phone required to Login", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }
}