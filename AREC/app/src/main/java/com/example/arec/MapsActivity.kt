package com.example.arec

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.arec.databinding.ActivityMapsBinding
import com.example.arec.databinding.LoginBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import kotlin.math.pow
import kotlin.math.sqrt

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.model.Marker


class MapsActivity : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var userMaker : Marker? = null
    private lateinit var binding: ActivityMapsBinding
    private lateinit var latLngUser : LatLng
    private lateinit var user : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ActivityMapsBinding>(inflater, R.layout.activity_maps,container,false)

        binding.butAddEvent.setOnClickListener { view : View ->
            val bundleMapCreate = Bundle()
            bundleMapCreate.putParcelable("LatLngUser", latLngUser)
            view.findNavController().navigate(R.id.action_mapsFragment_to_createEvent, bundleMapCreate) }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // show the action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        inflater.inflate(R.menu.toolbar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to get the user's location again
                onMapReady(mMap)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mapFragment = childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment?
        if (mapFragment == null) {
            val transaction = childFragmentManager.beginTransaction()
            val newMapFragment = SupportMapFragment.newInstance()
            transaction.add(R.id.map, newMapFragment, "mapFragment")
            transaction.commit()
            mapFragment = newMapFragment
        }
        mapFragment.getMapAsync(this)
    }

    private val REQUEST_LOCATION_PERMISSION = 1
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            // remove these after u have the event dabase done
            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            // If we have a location, add a marker at the location
            if (location != null) {
                //same remover mais tarde so para teste
                val latLngEvent = LatLng(location.latitude, location.longitude )
                //temporary user Event static remover mais tarde so para teste
                val event = Event("event",1, 1, 1000.0, latLngEvent, 10.0, 7)

                ///////////////////////////

                // Set up the location listener to move the marker
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {

                        // Update the marker position User from the location of the callback
                        latLngUser = LatLng(location.latitude, location.longitude)

                        //clear preivious markers
                        //userMaker?.remove()
                        mMap.clear()

                        //add the marker for the user and event
                        userMaker = mMap.addMarker(MarkerOptions().position(latLngUser).title("User"))
                        user = User(1, "", 1, "", "", "", latLngUser)

                        createEventMarker(event)

                        Log.e("MyLogs", "$event" + "$user")
                    }

                    //perceber melhor para que que isto serve
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }

                // Request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

                // Move the camera to the user's location
                //change to LatLnGEven, but the useer position takes a bit to load maybe make a loading screen to wait for the user position
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngEvent, 15f))
            }

        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    //Funtion to verify if the user can start connecting
    fun userInsideEvent(user : User, event : Event) : Boolean{
        val yEvent = event.getLongitude()
        val xEvent = event.getLatitude()
        val yUser = user.getLongitude()
        val xUser = user.getLatitude()
        val radius = event.getRadius()

        val distance = FloatArray(1)
        Location.distanceBetween(xUser, yUser, xEvent, yEvent, distance)
        val distanceInMeters = distance[0].toDouble()

        return radius >= distanceInMeters
    }

    fun createEventMarker(event: Event){
        val markerEvent = mMap.addMarker(MarkerOptions().position(event.getLatLng()).title(event.getEventName()))

        // Set an OnMarkerClickListener on the map for Events
        mMap.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker == markerEvent) {
                val bundleMapDescription = Bundle()
                bundleMapDescription.putBoolean("inside", userInsideEvent(user, event))
                findNavController().navigate(R.id.action_mapsFragment_to_eventDescription, bundleMapDescription)
                true  // Return true to indicate that the event has been handled
            } else {
                false  // Return false to indicate that the event has not been handled
            }
        }

        // Add a circle around the marker position
        val circleOptions = CircleOptions()
            .center(event.getLatLng())
            .radius(event.getRadius())  // radius in meters
            .strokeWidth(2f)
            .strokeColor(Color.BLUE)
            .fillColor(Color.argb(70, 0, 0, 200))
        mMap.addCircle(circleOptions)
    }



}