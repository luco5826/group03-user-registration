package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.enums.UserValidationStatus

data class RegisterDTO(
    val status: UserValidationStatus,
    val activation: ActivationDTO?
)
