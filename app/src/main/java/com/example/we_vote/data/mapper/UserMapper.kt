package com.example.we_vote.data.mapper

import com.example.we_vote.data.remote.dto.UserDto
import com.example.we_vote.domain.model.User

fun UserDto.toDomain() = User(
    name = name,
    email = email,
    dob = dob,
    city = city,
    password = password,
    access = access,
)

fun User.toDto() = UserDto(
    name = name,
    email = email,
    dob = dob,
    city = city,
    password = password,
    access = access,
)
