package com.example.arec

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.viewpager.widget.ViewPager
import com.example.arec.adapter.ImagePagerAdapter
import com.example.arec.databinding.ProfileBinding
import com.example.arec.model.Event
import com.example.arec.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class   Profile : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var binding: ProfileBinding
    private lateinit var auth: FirebaseAuth
    private var joinedEvent: Boolean = false
    private lateinit var userLogged : User
    private val userList = mutableListOf<User>() //Participants on event real Time
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ProfileBinding>(inflater, R.layout.profile,container,false)

        binding.butEdit.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_profileFragment_to_editProfile) }
        
        binding.logoutAccount.setOnClickListener {view : View ->
            auth.signOut()
            view.findNavController().navigate(R.id.action_profile_to_login)
        }

        auth = FirebaseAuth.getInstance()

        val source = arguments?.getString("source")
        if (source == "joinedEvent") {
            binding.butEdit.setVisibility(View.GONE)
            binding.logoutAccount.setVisibility(View.GONE)
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

        if(joinedEvent) {

            val eventID = arguments?.getString("EventId")
            val databaseReference =
                FirebaseDatabase.getInstance().reference.child("events").child(eventID!!)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // dataSnapshot will contain the data for the child with the specified ID
                    if (dataSnapshot.exists()) {
                        // Retrieve the data from the snapshot and perform the desired operations
                        val event = dataSnapshot.getValue(Event::class.java)

                        FirebaseDatabase.getInstance().reference.child("users")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    userList.clear()
                                    for (snapshot1 in snapshot.children) {
                                        val user = snapshot1.getValue(User::class.java)
                                        if(user!!.uid == auth.currentUser?.uid!!) {
                                            userLogged = user
                                            break
                                        }
                                        Log.e("noob", userList.toString())
                                    }
                                    for (snapshot1 in snapshot.children) {
                                        val user = snapshot1.getValue(User::class.java)
                                        if (event!!.participants.contains(user!!.uid))
                                            if(user!!.uid != auth.currentUser?.uid!!)
                                                if(userLogged.show.equals("males") && user.gender == "male" ||
                                                    userLogged.show.equals("females") && user.gender == "female" ||
                                                    userLogged.show.equals("everyone") ||
                                                    userLogged.show.equals("everyone") && user.gender =="other")
                                                    userList.add(user)
                                        Log.e("noob", userList.toString()   )
                                    }

                                    val avatarResourceId = R.drawable.avatar
                                    val avatarUrl = "android.resource://" + context!!.packageName + "/" + avatarResourceId

                                    userList.add(User("No users in the event:", "", "0", 0, "", "", "" ,avatarUrl))
                                    var currentUserIndex = 0

                                    // Update the adapter with the current user data
                                    val images = userList[currentUserIndex].profileImage
                                    val adapter = ImagePagerAdapter(images, requireContext())
                                    viewPager.adapter = adapter
                                    binding.profileAge.text = userList[currentUserIndex].age.toString()
                                    binding.profileName.text = userList[currentUserIndex].name
                                    binding.profileDescription.text = userList[currentUserIndex].description
                                    binding.profileGender.text = userList[currentUserIndex].gender

                                    // Handle button click events
                                    binding.butLike.setOnClickListener {
                                        // Move to the next user when Like button is clicked
                                        if(userLogged.likedYou.contains(userList[currentUserIndex].uid) && !userLogged.matched.contains(userList[currentUserIndex].uid!!)
                                            && userList[currentUserIndex].uid != "0") {
                                            userLogged.matched.add(userList[currentUserIndex].uid!!)
                                            var userReference =
                                                FirebaseDatabase.getInstance().reference.child("users").child(userLogged.uid!!)
                                            userReference.setValue(userLogged)

                                            userList[currentUserIndex].matched.add(userLogged.uid!!)
                                            userReference =
                                                FirebaseDatabase.getInstance().reference.child("users")
                                                    .child(userList[currentUserIndex].uid!!)
                                            userReference.setValue(userList[currentUserIndex])
                                                .addOnSuccessListener {}
                                        }
                                        else if(!userList[currentUserIndex].likedYou.contains(userLogged.uid!!) && userList[currentUserIndex].uid != "0"){
                                            userList[currentUserIndex].likedYou.add(userLogged.uid!!)
                                            val userReference =
                                                FirebaseDatabase.getInstance().reference.child("users")
                                                    .child(userList[currentUserIndex].uid!!)
                                            userReference.setValue(userList[currentUserIndex])

                                        }
                                        if(currentUserIndex < userList.size -1){
                                            currentUserIndex++
                                            // Update the adapter with the next user data
                                            val images =
                                                userList[currentUserIndex].profileImage
                                            val adapter =
                                                ImagePagerAdapter(images, requireContext())
                                            viewPager.adapter = adapter
                                            binding.profileAge.text = userList[currentUserIndex].age.toString()
                                            binding.profileName.text = userList[currentUserIndex].name
                                            binding.profileDescription.text = userList[currentUserIndex].description
                                            binding.profileGender.text = userList[currentUserIndex].gender
                                        }
                                        else{
                                            Toast.makeText(requireContext(), "No users on the Event Try again Later!", Toast.LENGTH_SHORT).show()
                                            binding.profileAge.setVisibility(View.GONE)
                                        }
                                    }
                                    binding.butDislike.setOnClickListener {
                                        // Move to the next user when Dislike button is clicked
                                        if (currentUserIndex < userList.size - 1) {
                                            currentUserIndex++
                                            // Update the adapter with the next user data
                                            val images =
                                                userList[currentUserIndex].profileImage
                                            val adapter =
                                                ImagePagerAdapter(images, requireContext())
                                            viewPager.adapter = adapter
                                            binding.profileAge.text = userList[currentUserIndex].age.toString()
                                            binding.profileName.text = userList[currentUserIndex].name
                                            binding.profileDescription.text = userList[currentUserIndex].description
                                            binding.profileGender.text = userList[currentUserIndex].gender
                                        }
                                        else{
                                            Toast.makeText(requireContext(), "No users on the Event Try again Later!", Toast.LENGTH_SHORT).show()
                                            binding.profileAge.setVisibility(View.GONE)
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
        else{
            val databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser?.uid!!)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // dataSnapshot will contain the data for the child with the specified ID
                    if (dataSnapshot.exists()) {
                        val currUser = dataSnapshot.getValue(User::class.java)
                        val images = currUser!!.profileImage
                        val adapter = ImagePagerAdapter(images, requireContext())
                        viewPager.adapter = adapter

                        binding.profileAge.text = currUser.age.toString()
                        binding.profileName.text = currUser.name
                        binding.profileDescription.text = currUser.description
                        binding.profileGender.text = currUser.gender
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