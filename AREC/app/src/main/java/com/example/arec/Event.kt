package com.example.arec

import com.google.android.gms.maps.model.LatLng
import kotlin.time.Duration

class Event(
    private var eventName: String,
    private val ID: Int,                    //ID of the event
    private val eventOwner: Int,            //ID of the user
    private var radius: Double,             //radius of the Event
    private val latlng: LatLng,             //Center
    private var duration: Double,           //duration of the event
    private var totalParticipants: Int){    // Participants that the event can accept

    private val lat: Double = this.latlng.latitude
    private val long: Double = this.latlng.latitude
    private val participants = mutableSetOf<Int>()        //Participants on event real Time

    //toString
    override fun toString(): String {
        return "Event(ID=$ID, eventOwner=$eventOwner, radius=$radius, " +
                "lat=$lat, long=$long, duration=$duration, " +
                "totalParticipants=$totalParticipants, " +
                "participants=$participants)\n"
    }

    // Getter methods

    fun getEventName(): String {
        return this.eventName
    }

    fun getID(): Int {
        return this.ID
    }

    fun getEventOwner(): Int {
        return this.eventOwner
    }

    fun getRadius(): Double {
        return this.radius
    }

    fun getLatitude(): Double {
        return this.lat
    }

    fun getLongitude(): Double {
        return this.long
    }

    fun getDuration(): Double {
        return this.duration
    }

    fun getTotalParticipants(): Int {
        return this.totalParticipants
    }

    // Setter methods
    fun setEventName(eventName: String){
        this.eventName = eventName
    }

    fun setRadius(radius : Double){
        this.radius = radius
    }

    fun setDuration(duration: Double){
        this.duration = duration
    }

    fun setTotalParticipants(total: Int) {
        this.totalParticipants = total
    }

    // participants methods
    fun addParticipant(participantId: Int) {
        participants.add(participantId)
    }

    fun removeParticipant(participantId: Int) {
        participants.remove(participantId)
    }

    fun hasParticipant(participantId: Int): Boolean {
        return participants.contains(participantId)
    }

    fun getNumParticipants(): Int {
        return participants.size
    }


}