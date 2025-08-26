package com.kev.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String,
    val fullName: String,
    val username: String,
    val gender: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val city: String,
    val country: String,
    val age: Int,
    val registeredDate: String,
) : Parcelable