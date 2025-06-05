package com.hongul.fliq.api.user.dto

import com.hongul.fliq.model.user.User

data class GetUserResponse(
    val id: Long,
    val name: String,
    val email: String = "",
    val profileImageURL: String = ""
) {
    fun toUser() = User(
        id = id,
        name = name,
        email = email,
        profileImageURL = profileImageURL
    )
}