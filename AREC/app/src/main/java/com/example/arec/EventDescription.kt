package com.example.arec

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arec.databinding.EventDecriptionBinding
import com.example.arec.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventDescription : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<EventDecriptionBinding>(inflater, R.layout.event_decription,container,false)
        setHasOptionsMenu(true)

        val eventID = arguments?.getString("EventId")
        if(eventID != null){
            val databaseReference = FirebaseDatabase.getInstance().reference.child("events")
            .child(eventID!!)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // dataSnapshot will contain the data for the child with the specified ID
                    if (dataSnapshot.exists()) {
                        // Retrieve the data from the snapshot and perform the desired operations
                        val event = dataSnapshot.getValue(Event::class.java)
                        binding.eventTitle.text = event!!.eventName
                        binding.eventDurationOut.text = event.duration.toString()
                        binding.eventRadiusOut.text = event.radius.toString()
                        binding.eventParticipantsOut.text = event.participants.size.toString()

                        // Retrieve the boolean to verify if an event is inside from the arguments
                        val data = arguments?.getBoolean("inside")
                        if(data!!){
                            binding.butJoin.setOnClickListener { view : View ->
                                event.participants.add(FirebaseAuth.getInstance().uid!!)
                                dataSnapshot.ref.setValue(event)

                                val bundleDescriptionProfile = Bundle()
                                bundleDescriptionProfile.putString("source", "joinedEvent")
                                view.findNavController().navigate(R.id.action_eventDescription_to_profileFragment, bundleDescriptionProfile) }
                        }
                        else{
                            binding.butJoin.setOnClickListener { view : View ->
                                Toast.makeText(context, "Can't Join out of range", Toast.LENGTH_LONG).show();
                                view.findNavController().navigate(R.id.action_eventDescription_to_mapsFragment) }
                        }
                        Log.i("noob", "is It in?" + data)
                    } else {
                        // Child with the specified ID does not exist in the database
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that may occur while retrieving the data
                }
            })
        }




        return binding.root
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