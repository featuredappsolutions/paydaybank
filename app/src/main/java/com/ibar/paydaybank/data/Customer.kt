package com.ibar.paydaybank.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Customer(@SerializedName("First Name") val firstName: String, @SerializedName("Last Name") val lastName: String,
    val gender: String, val email: String, val password: String, val dob: String, val phone: String, var id: Long
) : Serializable