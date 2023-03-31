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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class MapsActivity : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ActivityMapsBinding>(inflater, R.layout.activity_maps,container,false)

        binding.butAddEvent.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_mapsActivity_to_createEvent) }

        return binding.root
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            // Get the user's current location
            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            // If we have a location, add a marker at the location
            if (location != null) {

                // Add the first marker
                val latLngEvent = LatLng(location.latitude + 0.1, location.longitude + 0.1)

                // Set up the location listener to move the marker and validate if the user is inside the circle
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        // Update the marker position User
                        val latLngUser = LatLng(location.latitude, location.longitude)
                        mMap.clear()

                        //add the marker for the user and event
                        mMap.addMarker(MarkerOptions().position(latLngUser).title("My Position"))
                        val marker = mMap.addMarker(MarkerOptions().position(latLngEvent).title("Event"))

                        // Set an OnMarkerClickListener on the map
                        mMap.setOnMarkerClickListener { clickedMarker ->
                            if (clickedMarker == marker) {
                                // Handle the marker click event here
                                // This code will be executed when the user clicks on the marker
                                //Toast.makeText(requireContext(), "Your message here", Toast.LENGTH_SHORT).show();
                                findNavController().navigate(R.id.action_mapsActivity_to_eventDescription)
                                true  // Return true to indicate that the event has been handled
                            } else {
                                false  // Return false to indicate that the event has not been handled
                            }
                        }


                        // Add a circle around the marker position
                        val circleOptions = CircleOptions()
                            .center(latLngEvent)
                            .radius(1000.0)  // radius in meters
                            .strokeWidth(2f)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(70, 0, 0, 200))
                        mMap.addCircle(circleOptions)

                        // Check if the user is inside the circle
                        val user = User(1, "", 1, "", "", "", latLngUser)
                        val event = Event("event",1, 1, 1000.0, latLngEvent, 10.0, 7)
                        Log.i("MyPosition", "isIt in?" + userInsideEvent(user, event))
                    }

                    //perceber melhor para que que isto serve
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }

                // Request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

                // Move the camera to the user's location
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
    //mudar parametros so para user, event
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


}