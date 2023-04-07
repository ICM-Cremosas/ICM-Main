package com.example.arec

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.arec.adapter.MessageAdapter
import com.example.arec.databinding.ActivityChatBinding
import com.example.arec.databinding.ActivityUsersBinding
import com.example.arec.databinding.AskLocBinding
import com.example.arec.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
//import kotlinx.coroutines.NonCancellable.message
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : Fragment() {

    lateinit var binding: ActivityChatBinding
    var adapter : MessageAdapter? = null
    var messages:ArrayList<Message>? = null
    var senderRoom: String? = null
    var receiverRoom: String? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var dialog: ProgressDialog? = null
    var senderUid: String? = null
    var receiverUid: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ActivityChatBinding>(inflater, R.layout.activity_chat,container,false)
        //tirei isto n sei se faz dif tava a dar erro
        //(requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbar)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        messages = ArrayList()
        val name = arguments?.getString("name")
        if(name != null)
            binding!!.name.text = name

        val profile = arguments?.getString("image")
        if(profile != null)
            Glide.with(requireContext()).load(profile)
                .placeholder(R.drawable.placeholder)
                .into(binding!!.profile01)
        binding!!.imageView2.setOnClickListener {
            findNavController().navigate(R.id.action_chatActivity_to_usersActivity)
        }
        receiverUid = arguments?.getString("uid")
        if(receiverUid != null) {
            senderUid = FirebaseAuth.getInstance().uid
            database!!.reference.child("Presence").child(receiverUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val status = snapshot.getValue(String::class.java)
                            if (status == "offline") {
                                binding!!.status.visibility = View.GONE
                            } else {
                                binding!!.status.setText(status)
                                binding!!.status.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}

                })


            senderRoom = senderUid + receiverUid
            receiverRoom = receiverUid + senderUid
            adapter = MessageAdapter(requireContext(), messages, senderRoom!!, receiverRoom!!)

            binding!!.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding!!.recyclerView.adapter = adapter
            database!!.reference.child("chats")
                .child(senderRoom!!)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messages!!.clear()
                        for (snapshot1 in snapshot.children) {
                            val message: Message? = snapshot1.getValue(Message::class.java)
                            message!!.messageId = snapshot1.key
                            messages!!.add(message)
                            Log.d("noob1", message.toString())
                        }
                        adapter!!.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            binding!!.sendBtn.setOnClickListener {
                val messageTxt: String = binding!!.messageBox.text.toString()
                val date = Date()
                val message = Message(messageTxt, senderUid, date.time)

                binding!!.messageBox.setText("")
                val randomKey = database!!.reference.push().key
                val lastMsgObj = HashMap<String, Any>()
                lastMsgObj["lastMsg"] = message.message!!
                lastMsgObj["lastMsgTime"] = date.time

                database!!.reference.child("chats").child(senderRoom!!)
                    .updateChildren(lastMsgObj)
                database!!.reference.child("chats").child(receiverRoom!!)
                    .updateChildren(lastMsgObj)
                database!!.reference.child("chats").child(senderRoom!!)
                    .child("messages")
                    .child(randomKey!!)
                    .setValue(message).addOnCanceledListener {
                        database!!.reference.child("chats")
                            .child(receiverRoom!!)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message)
                            .addOnSuccessListener { }
                    }
            }
            binding!!.attachment.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 25)
            }

            val handler = Handler()
            binding!!.messageBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    database!!.reference.child("Presence")
                        .child(senderUid!!)
                        .setValue("typing...")
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(userStoppedTyping, 1000)
                }

                var userStoppedTyping = Runnable {
                    database!!.reference.child("Presence")
                        .child(senderUid!!)
                        .setValue("Online")
                }

            })
            //(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            if (data != null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    var reference = storage!!.reference.child("chats")
                        .child(calendar.timeInMillis.toString()+"")
                    dialog!!.show()
                    reference.putFile(selectedImage!!)
                        .addOnCompleteListener { task ->
                            dialog!!.dismiss()
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->
                                    val filePath = uri.toString()
                                    val messageTxt :String = binding!!.messageBox.text.toString()
                                    val date = Date()
                                    val message = Message(messageTxt, senderUid, date.time)
                                    message.message = "photo"
                                    message.imageUrl = filePath
                                    binding!!.messageBox.setText("")
                                    val randomkey = database!!.reference.push().key
                                    val lastMsgObj = HashMap<String,Any>()
                                    lastMsgObj["lastMsg"] = message.message!!
                                    lastMsgObj["lastMsgTime"] = date.time
                                    database!!.reference.child("chats")
                                        .updateChildren(lastMsgObj)
                                    database!!.reference.child("chats")
                                        .child(receiverRoom!!)
                                        .updateChildren(lastMsgObj)
                                    database!!.reference.child("chats")
                                        .child(senderRoom!!)
                                        .child("messages")
                                        .child(randomkey!!)
                                        .setValue(message).addOnSuccessListener {
                                            database!!.reference.child("chats")
                                                .child(receiverRoom!!)
                                                .child("messages")
                                                .child(randomkey)
                                                .setValue(message)
                                                .addOnSuccessListener {  }

                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("offline")
    }
}