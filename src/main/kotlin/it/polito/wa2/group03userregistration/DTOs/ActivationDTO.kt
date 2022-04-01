package it.polito.wa2.group03userregistration.DTOs

import it.polito.wa2.group03userregistration.entities.Activation

data class ActivationDTO(val attempt: Int) {
}

fun Activation.DTO() = ActivationDTO(attempt)