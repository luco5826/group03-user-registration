package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.entities.User

data class UserDTO(val username: String, val email: String)

fun User.toUserDTO() = UserDTO(username, email)
