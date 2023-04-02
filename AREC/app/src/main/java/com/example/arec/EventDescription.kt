package com.example.arec

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arec.databinding.EventDecriptionBinding

class EventDescription : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<EventDecriptionBinding>(inflater, R.layout.event_decription,container,false)
        setHasOptionsMenu(true)

        // Retrieve the bundle from the arguments
        val data = arguments?.getBoolean("inside")
        if (data != null) {
            if(data){
                binding.butJoin.setOnClickListener { view : View ->
                    val bundleDescriptionProfile = Bundle()
                    bundleDescriptionProfile.putString("source", "description")
                    view.findNavController().navigate(R.id.action_eventDescription_to_profileFragment, bundleDescriptionProfile) }
            }
            else{
                binding.butJoin.setOnClickListener { view : View ->
                    Toast.makeText(context, "Can't Join out of range", Toast.LENGTH_LONG).show();
                    view.findNavController().navigate(R.id.action_eventDescription_to_mapsFragment) }
            }
            Log.i("MyLogs", "is It in?" + data)
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