package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.entities.User

data class UserDTO(val userId: Long?, val username: String, val email: String, val password: String?)

fun User.toUserDTO() = UserDTO(id, username, email, password)
