package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.entities.Activation
import java.util.*

data class ActivationDTO(val provisionalId: UUID, val email: String?, val activationCode: String?)

fun Activation.toDTO() = ActivationDTO(id!!, userActivation.email, activationCode)
