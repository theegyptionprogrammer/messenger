package com.example.ourmessenger.modules

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String, val username: String, var selectedPhotoUrl: String) : Parcelable {
    constructor() : this ("" , "" , "")
}
