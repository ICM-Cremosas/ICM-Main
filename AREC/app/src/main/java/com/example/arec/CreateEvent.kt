package com.example.arec

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arec.databinding.CreateEventBinding
import com.example.arec.model.Event
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateEvent : Fragment() {

    var database: FirebaseDatabase? = null
    lateinit var binding : CreateEventBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<CreateEventBinding>(inflater, R.layout.create_event,container,false)

        database = FirebaseDatabase.getInstance()

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val latlng = arguments?.getParcelable<LatLng>("LatLngUser")
        if (latlng != null){
            binding.butCreateEvent.setOnClickListener {
                val eventName = binding.eventName.text.toString()
                val radius = binding.eventRadius.text.toString().toDouble()
                val duration = binding.eventDuration.text.toString().toDouble()
                val totalParticipants = binding.eventParticipants.text.toString().toInt()
                val randomKey = database!!.reference.push().key
                val event = Event(eventName, randomKey!! , FirebaseAuth.getInstance().uid!!, radius, latlng, duration, totalParticipants)

                database!!.reference
                    .child("events")
                    .child(randomKey!!)
                    .setValue(event)
                    .addOnCompleteListener {
                        view?.findNavController()?.navigate(R.id.action_createEvent_to_mapsFragment)
                    }
            }
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