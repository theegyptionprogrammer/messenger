package com.example.ourmessenger

data class User(val uid: String, val username: String, val selectedPhotoUrl: String){
    constructor() : this ("" , "" , "")
}
