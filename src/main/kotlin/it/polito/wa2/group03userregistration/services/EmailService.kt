package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import it.polito.wa2.group03userregistration.dtos.DTO
import it.polito.wa2.group03userregistration.entities.Activation
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.repositories.ActivationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EmailService {

    private val ACTIVATIONCODE_LENGTH = 10
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')+('0'..'9')
    @Autowired
    lateinit var activationRepository: ActivationRepository
    fun insertActivation(user: User): ActivationDTO {
        val savedEntity = activationRepository.save(Activation(user, generateActivationCode()))
        return savedEntity.DTO()
    }

    fun generateActivationCode(): String {
        return (1..ACTIVATIONCODE_LENGTH)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map { charPool[it] }
            .joinToString("")
    }

}