package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.enums.ActivationStatus

data class ValidateDTO(
    val status: ActivationStatus,
    val user: UserDTO?
)
