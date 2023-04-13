package com.example.arec

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.arec.databinding.RegisterBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Register : Fragment() {

    var database: FirebaseDatabase? = null
    lateinit var binding : RegisterBinding
    var selectedImage: String? = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<RegisterBinding>(inflater, R.layout.register,container,false)

        // hide the action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()

        binding.profileImage.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,45)
        }

        binding.btnSignup.setOnClickListener {view : View ->
            val bundleRegisterOtp = Bundle()
            val name = binding.edtName.text.toString()
            val phone = binding.edtPhone.text.toString()
            if(name != "" && phone != "" && selectedImage != ""){
                bundleRegisterOtp.putString("phone", phone)
                bundleRegisterOtp.putString("name", name)
                bundleRegisterOtp.putString("selectedImage", selectedImage)
                view.findNavController().navigate(R.id.action_register_to_OTP, bundleRegisterOtp)
            }
            else{
                Toast.makeText(requireContext(), "All fields Must be filled and Photo selected", Toast.LENGTH_LONG).show()
            }


        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if(data.data != null) {
                val uri = data.data // filePath
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference
                    .child("Profile")
                    .child(time.toString() + "")
                reference.putFile(uri!!).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener{ uri ->
                            selectedImage = uri.result.toString()
                            Log.e("noob", selectedImage!!)
                        }
                    }
                }

                binding!!.profileImage.setImageURI(data.data)


            }
        }
    }


}