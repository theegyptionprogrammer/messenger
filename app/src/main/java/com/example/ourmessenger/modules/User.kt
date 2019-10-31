package com.example.ourmessenger.modules

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String, val username: String, val selectedPhotoUrl: String) : Parcelable {
    constructor() : this ("" , "" , "")
}
