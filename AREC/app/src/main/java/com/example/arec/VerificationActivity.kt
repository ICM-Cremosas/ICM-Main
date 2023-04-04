package com.example.arec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.findNavController
import com.example.arec.databinding.ActivityVerificationBinding
import com.example.arec.databinding.AskLocBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.Fragment

class VerificationActivity : Fragment() {

    var binding : ActivityVerificationBinding? = null

    var auth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<ActivityVerificationBinding>(inflater, R.layout.activity_verification,container,false)

        auth = FirebaseAuth.getInstance()
        if (auth!!.currentUser != null) {
            //val intent = Intent(this@VerificationActivity, MainActivity::class.java)
            //startActivity(intent)
            //finish()
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding!!.editNumber.requestFocus()
        binding!!.continueBtn.setOnClickListener{
            //val intent = Intent(this@VerificationActivity, OTPActivity::class.java)

            //intent.putExtra("phoneNumber", binding!!.editNumber.text.toString())

            //startActivity(intent)

        }

        return binding.root
    }

}