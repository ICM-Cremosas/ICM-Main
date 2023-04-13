package com.example.arec

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.arec.databinding.ActivitySetupProfileBinding
import com.example.arec.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class SetupProfileActivity : Fragment() {
    lateinit var binding: ActivitySetupProfileBinding
    var auth: FirebaseAuth? = null
    var database:FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ActivitySetupProfileBinding>(inflater, R.layout.activity_setup_profile,container,false)
        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Updating Profile...")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding!!.imageView.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,45)
        }
        binding!!.continueBtn02.setOnClickListener {
            val name: String = binding!!.nameBox.text.toString()
            if (name.isEmpty()) {
                binding!!.nameBox.setError("Please type a name")
            }
            dialog!!.show()
            if(selectedImage != null) {
                val reference = storage!!.reference.child("Profile").child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val imageUrl = uri.result.toString()
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val name: String = binding!!.nameBox.text.toString()
                            Log.i("noob", imageUrl)
                            val user = User(uid, name, phone, imageUrl)
                            database!!.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCompleteListener {
                                    dialog!!.dismiss()
                                }
                        }
                    }
                    else {
                        val uid = auth!!.uid
                        val phone = auth!!.currentUser!!.phoneNumber
                        val name: String = binding!!.nameBox.text.toString()
                        val user = User(uid,name,phone, "No Image")
                        database!!.reference
                            .child("users")
                            .child(uid!!)
                            .setValue(user)
                            .addOnCanceledListener {
                                dialog!!.dismiss()
                            }
                    }
                }
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
                            val filePath = uri.toString()
                            val obj = HashMap<String,Any>()
                            obj["image"] = filePath
                            database!!.reference
                                .child("users")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnSuccessListener {  }
                        }
                    }
                }

                binding!!.imageView.setImageURI(data.data)
                selectedImage = data.data

            }
        }
    }


}