package com.kev.data.mapper

import com.kev.data.datasource.local.ContactEntity
import com.kev.data.model.ContactDto
import com.kev.domain.model.Contact

object ContactMapper {

    fun ContactDto.toContactEntity(): ContactEntity {
        val fullName = listOf(name.title, name.first, name.last)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        return ContactEntity(
            id = login.uuid,
            fullName = fullName,
            email = email,
            phone = phone,
            avatarUrl = picture.large,
            country = location.country,
            city = location.city,
            gender = gender,
            age = dob.age,
            registeredDate = registered.date,
            username = login.username
        )
    }

    fun ContactEntity.toDomain(): Contact {
        return Contact(
            id = id,
            fullName = fullName,
            email = email,
            phone = phone,
            avatarUrl = avatarUrl,
            country = country,
            city = city,
            gender = gender,
            age = age,
            registeredDate = registeredDate,
            username = username
        )
    }
}