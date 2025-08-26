package com.kev.data.model

data class ContactListResponse(
    val results: List<ContactDto>,
    val info: InfoDto
)

data class InfoDto(
    val seed: String,
    val results: Int,
    val page: Int,
    val version: String
)