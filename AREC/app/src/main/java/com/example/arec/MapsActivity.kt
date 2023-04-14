package com.example.arec

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arec.databinding.ActivityMapsBinding
import com.example.arec.model.Event
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MapsActivity : Fragment(), OnMapReadyCallback {

    private var progressDialog: ProgressDialog? = null
    private lateinit var mMap: GoogleMap
    private var userMaker : Marker? = null
    private lateinit var binding: ActivityMapsBinding
    private lateinit var latLngUser : LatLng
    var database: FirebaseDatabase? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ActivityMapsBinding>(inflater, R.layout.activity_maps,container,false)

        database = FirebaseDatabase.getInstance()
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
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setMessage("Loading Map...")
        progressDialog?.show()
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

            latLngUser = LatLng(location!!.latitude, location!!.longitude)



            // Example usage in your MapsActivity
            val icon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_person_map))
            var userMaker = mMap.addMarker(MarkerOptions().position(latLngUser).title("User").icon(icon))

            database!!.reference.child("events")
                .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mMap.clear()
                    for(snapshot1 in snapshot.children) {
                        val event: Event? = snapshot1.getValue(Event::class.java)
                        createEventMarker(event!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
            // Set up the location listener to move the marker
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Update the marker position User from the location of the callback
                    latLngUser = LatLng(location.latitude, location.longitude)

                    //clear preivious markers
                    userMaker?.remove()

                    //add the marker for the user and event
                    userMaker = mMap.addMarker(MarkerOptions().position(latLngUser).title("User").icon(icon))
                    userMaker!!.tag = "user"

                    if (progressDialog?.isShowing == true) {
                        progressDialog?.dismiss()
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngUser, 15f))
                    }

                }

                //perceber melhor para que que isto serve
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }

        mMap.setOnMarkerClickListener { clickedMarker ->
            if((clickedMarker.tag as String) != "user") {
                val databaseReference = FirebaseDatabase.getInstance().reference.child("events")
                    .child(clickedMarker.tag as String)

                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // dataSnapshot will contain the data for the child with the specified ID
                        if (dataSnapshot.exists()) {
                            // Retrieve the data from the snapshot and perform the desired operations
                            val event = dataSnapshot.getValue(Event::class.java)
                            val bundleMapDescription = Bundle()
                            bundleMapDescription.putString("EventId", event!!.ID)
                            bundleMapDescription.putBoolean("inside", userInsideEvent(event!!))
                            findNavController().navigate(
                                R.id.action_mapsFragment_to_eventDescription,
                                bundleMapDescription
                            )
                        } else {
                            // Child with the specified ID does not exist in the database
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any errors that may occur while retrieving the data
                    }
                })
                true
            }
            else{
                false
            }
        }
    }

    //Funtion to verify if the user can start connecting
    fun userInsideEvent(event : Event) : Boolean{
        val yEvent = event.longitude
        val xEvent = event.latitude
        val yUser = latLngUser.longitude
        val xUser = latLngUser.latitude
        val radius = event.radius

        val distance = FloatArray(1)
        Location.distanceBetween(xUser, yUser, xEvent, yEvent, distance)
        val distanceInMeters = distance[0].toDouble()

        return radius >= distanceInMeters
    }

    fun createEventMarker(event: Event){
        var center = LatLng(event.latitude, event.longitude)
        val markerEvent = mMap.addMarker(MarkerOptions().position(center).title(event.eventName))
        markerEvent!!.tag = event.ID


        // Add a circle around the marker position
        val circleOptions = CircleOptions()
            .center(center)
            .radius(event.radius)  // radius in meters
            .strokeWidth(2f)
            .strokeColor(Color.BLUE)
            .fillColor(Color.argb(70, 0, 0, 200))
        mMap.addCircle(circleOptions)
    }

    // Function to convert a vector drawable to a Bitmap
    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 0,
            drawable?.intrinsicHeight ?: 0,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }


}