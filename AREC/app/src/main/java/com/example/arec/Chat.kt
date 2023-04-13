package com.example.arec

//import kotlinx.coroutines.NonCancellable.message
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arec.adapter.MessageAdapter
import com.example.arec.databinding.ChatBinding
import com.example.arec.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Chat : Fragment() {

    lateinit var binding: ChatBinding
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var database: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ChatBinding>(inflater, R.layout.chat,container,false)
        val name = arguments?.getString("name")
        val receiverUid = arguments?.getString("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        (requireActivity() as AppCompatActivity).supportActionBar?.title = name

        chatRecyclerView = binding.chatRecyclerView
        sendButton = binding.sentButton
        messageBox = binding.messageBox
        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(),messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = messageAdapter

        database.child("chat").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            database.child("chat").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    database.child("chat").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("")
        }
        return binding.root
    }

}
