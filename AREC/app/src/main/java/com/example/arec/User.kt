package com.example.arec

import android.provider.CalendarContract
import com.google.android.gms.maps.model.LatLng

class User(
    val ID: String,
    var name: String,
    var age: Int,
    var gender: String,
    var sexualOrientation : String,
    var bio: String,
    var latlng: LatLng){

    private val events = mutableSetOf<Int>()
    private val connections = mutableSetOf<Int>()

    //toString
    override fun toString(): String {
        return "User(ID=$ID, name='$name', age=$age, gender='$gender', " +
                "sexualOrientation='$sexualOrientation', bio='$bio', " +
                "lat=${latlng.latitude}, long=${latlng.longitude}, ,Events=${events}, connections=$connections)\n"
    }

}
