package it.polito.wa2.group03userregistration.repositories

import it.polito.wa2.group03userregistration.entities.Activation
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ActivationRepository: CrudRepository<Activation, UUID> {
}