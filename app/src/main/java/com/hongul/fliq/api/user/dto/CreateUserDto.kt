package com.hongul.fliq.api.user.dto

data class CreateUserBody(
    val id: Long,
    val name: String,
    val email: String = "",
    val profileImageURL: String = ""
)

data class CreateUserResponse(
    val success: Boolean,
    val data: Long,
    val message: String = ""
)


