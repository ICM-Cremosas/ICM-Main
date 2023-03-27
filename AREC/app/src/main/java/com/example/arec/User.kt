package com.example.arec

import android.provider.CalendarContract
import com.google.android.gms.maps.model.LatLng

class User(
    private val ID: Int,
    private var name: String,
    private var age: Int,
    private var gender: String,
    private var sexualOrientation : String,
    private var bio: String,
    private val latlng: LatLng){

    private var lat: Double = this.latlng.latitude
    private var long: Double = this.latlng.latitude
    private val events = mutableSetOf<Int>()
    private val connections = mutableSetOf<Int>()

    //toString
    override fun toString(): String {
        return "User(ID=$ID, name='$name', age=$age, gender='$gender', " +
                "sexualOrientation='$sexualOrientation', bio='$bio', " +
                "lat=$lat, long=$long, Events=${events}, connections=$connections)\n"
    }

    // Getter methods
    fun getID(): Int {
        return this.ID
    }

    fun getName(): String {
        return this.name
    }

    fun getAge(): Int {
        return this.age
    }

    fun getGender(): String {
        return this.gender
    }

    fun getSexualOrientation(): String {
        return this.sexualOrientation
    }

    fun getBio(): String {
        return this.bio
    }

    fun getLatitude(): Double {
        return this.lat
    }

    fun getLongitude(): Double {
        return this.long
    }

    // Setter methods
    fun setName(name: String) {
        this.name = name
    }

    fun setAge(age: Int) {
        this.age = age
    }

    fun setGender(gender: String) {
        this.gender = gender
    }

    fun setSexualOrientation(orientation: String) {
        this.sexualOrientation = orientation
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun setLatitude(latitude: Double) {
        this.lat = latitude
    }

    fun setLongitude(longitude: Double) {
        this.long = longitude
    }

    // Events methods
    fun addEvent(eventId: Int) {
        events.add(eventId)
    }

    fun removeEvent(eventId: Int) {
        events.remove(eventId)
    }

    fun hasEvent(eventId: Int): Boolean {
        return events.contains(eventId)
    }

    fun getNumEvents(): Int {
        return events.size
    }

    // Connections methods
    fun addConnection(userId: Int) {
        connections.add(userId)
    }

    fun removeConnection(userId: Int) {
        connections.remove(userId)
    }

    fun hasConnection(userId: Int): Boolean {
        return connections.contains(userId)
    }

    fun getNumConnections(): Int {
        return connections.size
    }

}