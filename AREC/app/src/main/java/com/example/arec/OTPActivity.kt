package com.example.arec

import android.app.ProgressDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.example.arec.databinding.ActivityOtpactivityBinding
import com.example.arec.databinding.ActivityVerificationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.arec.databinding.AskLocBinding

class OTPActivity : Fragment() {

    var binding : ActivityOtpactivityBinding? = null
    var verificationId:String? = null
    var auth: FirebaseAuth? = null
    var dialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<ActivityOtpactivityBinding>(inflater, R.layout.activity_otpactivity,container,false)

        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Sending OTP...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        auth = FirebaseAuth.getInstance()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val data = arguments?.getString("phoneNumber")
        if (data != null)
        {
            val phoneNumber = data
            binding!!.phoneLabel.text = "Verify $phoneNumber"

            val options = PhoneAuthOptions.newBuilder(auth!!)
                .setPhoneNumber(phoneNumber!!)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        TODO("Not yet implemented")
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        TODO("Not yet implemented")
                    }

                    override fun onCodeSent(
                        verifyId: String,
                        forceResendingToken: PhoneAuthProvider.ForceResendingToken
                    ) {
                        super.onCodeSent(verifyId, forceResendingToken)
                        dialog!!.dismiss()
                        verificationId = verifyId
                        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        binding!!.otpView.requestFocus()
                    }

                }).build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            binding!!.otpView.setOtpCompletionListener { otp ->
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
                auth!!.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            findNavController().navigate(R.id.action_OTPActivity_to_setupProfileActivity)
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        return binding.root
    }
}