package com.example.arec

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arec.databinding.CreateEventBinding
import com.google.android.gms.maps.model.LatLng

class CreateEvent : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<CreateEventBinding>(inflater, R.layout.create_event,container,false)

        val latlng = arguments?.getParcelable<LatLng>("LatLngUser")
        if (latlng != null){
            binding.butCreateEvent.setOnClickListener {
                val eventName = binding.eventName.text.toString()
                val radius = binding.eventRadius.text.toString().toDouble()
                val duration = binding.eventDuration.text.toString().toDouble()
                val totalParticipants = binding.eventParticipants.text.toString().toInt()

                val event = Event(eventName, 1, 1, radius, latlng, duration, totalParticipants)

                // add the event to the database
                Log.i("MyLogs", "Event->" + event)
                view?.findNavController()?.navigate(R.id.action_createEvent_to_mapsFragment)
            }
        }
        setHasOptionsMenu(true)
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