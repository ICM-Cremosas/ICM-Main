package com.example.arec

import ImagePagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.viewpager.widget.ViewPager
import com.example.arec.databinding.ProfileBinding
import com.example.arec.model.Event
import com.example.arec.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class   Profile : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var binding: ProfileBinding
    private var joinedEvent: Boolean = false
    val userList = mutableListOf<User>() //Participants on event real Time
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ProfileBinding>(inflater, R.layout.profile,container,false)

        binding.butEdit.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_profileFragment_to_editProfile) }

        val source = arguments?.getString("source")
        if (source == "joinedEvent") {
            binding.butEdit.setVisibility(View.GONE)
            joinedEvent = true
        } else{
            binding.butDislike.setVisibility(View.GONE)
            binding.butLike.setVisibility(View.GONE)
            joinedEvent = false
        }


        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.viewPager

        if(joinedEvent){

            val eventID = arguments?.getString("EventId")
            val databaseReference = FirebaseDatabase.getInstance().reference.child("events").child(eventID!!)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // dataSnapshot will contain the data for the child with the specified ID
                    if (dataSnapshot.exists()) {
                        // Retrieve the data from the snapshot and perform the desired operations
                        val event = dataSnapshot.getValue(Event::class.java)

                        FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(snapshot1 in snapshot.children) {
                                    val user = snapshot1.getValue(User::class.java)
                                    if(event!!.participants.contains(user!!.uid))
                                        userList.add(user)
                                    Log.e("noob", userList.toString())
                                }

                                var currentUserIndex = 0


                                // Update the adapter with the current user data
                                val images = listOf(userList[currentUserIndex].profileImage)
                                val adapter = ImagePagerAdapter(images, requireContext())
                                viewPager.adapter = adapter

                                // Handle button click events
                                binding.butLike.setOnClickListener {
                                    // Move to the next user when Like button is clicked
                                    if (currentUserIndex < userList.size - 1) {
                                        currentUserIndex++
                                        // Update the adapter with the next user data
                                        val images = listOf(userList[currentUserIndex].profileImage)
                                        val adapter = ImagePagerAdapter(images, requireContext())
                                        viewPager.adapter = adapter
                                    }
                                }
                                binding.butDislike.setOnClickListener {
                                    // Move to the next user when Dislike button is clicked
                                    if (currentUserIndex < userList.size - 1) {
                                        currentUserIndex++
                                        // Update the adapter with the next user data
                                        val images = listOf(userList[currentUserIndex].profileImage)
                                        val adapter = ImagePagerAdapter(images, requireContext())
                                        viewPager.adapter = adapter
                                    }
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {}

                        })
                    } else {
                        // Child with the specified ID does not exist in the database
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that may occur while retrieving the data
                }
            })
        }



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}