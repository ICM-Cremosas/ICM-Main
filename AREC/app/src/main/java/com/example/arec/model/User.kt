package com.example.arec.model

class User {
    var name: String? = null
    var phoneNumber: String? = null
    var uid: String? = null
    var profileImage:String? = null

    constructor(){}

    constructor(name: String?, phoneNumber: String?, uid: String?, profileImage: String?) {
        this.name = name
        this.phoneNumber = phoneNumber
        this.uid = uid
        this.profileImage = profileImage
    }
}