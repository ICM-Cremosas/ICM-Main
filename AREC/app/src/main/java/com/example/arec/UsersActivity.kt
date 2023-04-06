package com.example.arec

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.arec.adapter.UserAdapter
import com.example.arec.databinding.ActivityMainBinding
import com.example.arec.databinding.ActivitySetupProfileBinding
import com.example.arec.databinding.ActivityUsersBinding
import com.example.arec.databinding.AskLocBinding
import com.example.arec.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavController


class UsersActivity : Fragment() {

    var binding: ActivityUsersBinding? = null
    var database: FirebaseDatabase? = null
    var users: ArrayList<User>? = null
    var usersAdapter: UserAdapter? = null
    var dialog: ProgressDialog? = null
    var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ActivityUsersBinding>(inflater, R.layout.activity_users,container,false)

        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        users = ArrayList<User>()
        usersAdapter = UserAdapter(requireContext(), users!!, findNavController())
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding!!.mRec.layoutManager = layoutManager
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        binding!!.mRec.adapter = usersAdapter
        database!!.reference.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users!!.clear()
                for(snapshot1 in snapshot.children) {
                    val user:User? = snapshot1.getValue(User::class.java)
                    if(!(user!!.uid.equals(FirebaseAuth.getInstance().uid)))
                        users!!.add(user)
                    Log.e("noob", users!!.toString())
                }
                usersAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("offline ")

    }
}