package com.example.arec

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.viewpager.widget.ViewPager
import com.example.arec.databinding.ActivityMapsBinding
import com.example.arec.databinding.ProfileBinding

class Profile : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var binding: ProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ProfileBinding>(inflater, R.layout.profile,container,false)

        binding.butEdit.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_profileFragment_to_editProfile) }

        val source = arguments?.getString("source")
        if (source == "description") {
            binding.butEdit.setVisibility(View.GONE)
        } else{
            binding.butDislike.setVisibility(View.GONE)
            binding.butLike.setVisibility(View.GONE)
        }


        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.viewPager


        val images = listOf(R.drawable.ic_add, R.drawable.ic_info, R.drawable.ic_person)
        val adapter = ImagePagerAdapter(images, requireContext())
        viewPager.adapter = adapter


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