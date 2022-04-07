package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.services.ValidationStatus

data class RegisterDTO(val status: ValidationStatus,
                       val activation: ActivationDTO?)
