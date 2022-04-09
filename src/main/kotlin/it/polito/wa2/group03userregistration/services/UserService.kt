package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.*
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.ActivationStatus
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.repositories.ActivationRepository
import it.polito.wa2.group03userregistration.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class UserService {
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    val emailRegex =
        Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    fun registerUser(userToRegister: UserDTO): RegisterDTO {
        // From DTO to entity
        val user = User(userToRegister.username, userToRegister.password, userToRegister.email)

        val validationStatus = isValidUser(user)
        if (validationStatus != UserValidationStatus.VALID)
            return RegisterDTO(validationStatus, null)

        // If the user is valid then save their data in the DB
        // However the registration is not complete until activation with code
        user.salt = BCrypt.gensalt(10)
        user.password = BCrypt.hashpw(user.password, user.salt)
        val savedUser = userRepository.save(user)
        val activationDTO = emailService.insertActivation(savedUser)

        return RegisterDTO(validationStatus, activationDTO)
    }

    fun isValidUser(user: User): UserValidationStatus {
        user.username.isNotBlank() || return UserValidationStatus.NO_USERNAME
        user.email.isNotBlank() || return UserValidationStatus.NO_EMAIL
        user.password!!.isNotBlank() || return UserValidationStatus.NO_PASSWORD
        user.password!!.matches(passwordRegex) || return UserValidationStatus.WEAK_PASSWORD
        user.email.matches(emailRegex) || return UserValidationStatus.INVALID_EMAIL

        // TODO("Check is username and email are System Unique")

        return UserValidationStatus.VALID
    }

    fun validateUser(activation: ActivationDTO): ValidateDTO {
        val savedActivation = activationRepository.findById(activation.provisionalId).orElse(null)

        // Provisional id does not exist
        savedActivation == null &&
                return ValidateDTO(ActivationStatus.ID_DOES_NOT_EXIST, null)

        // Validation time expired
        if (savedActivation.expirationDate!!.before(Date.from(Instant.now()))) {
            activationRepository.delete(savedActivation)
            return ValidateDTO(ActivationStatus.EXPIRED, null)
        }

        // Wrong activation code
        if (activation.activationCode != savedActivation.activationCode) {
            savedActivation.attempt--

            if (savedActivation.attempt == 0) {
                activationRepository.delete(savedActivation)
                userRepository.delete(savedActivation.userActivation)
            } else {
                activationRepository.save(savedActivation)
            }
            return ValidateDTO(ActivationStatus.WRONG_ACTIVATION_CODE, null)
        }

        activationRepository.deleteById(activation.provisionalId)
        return ValidateDTO(ActivationStatus.SUCCESSFUL, savedActivation.userActivation.toUserDTO())
    }
}
