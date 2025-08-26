package com.kev.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val fullName: String,
    val gender: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val city: String,
    val country: String,
    val age: Int,
    val registeredDate: String,
    val username: String,
)