package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.RegisterDTO
import it.polito.wa2.group03userregistration.dtos.UserDTO
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class UserService {
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var userRepository: UserRepository

    fun registerUser(userToRegister: UserDTO): RegisterDTO {
        // From DTO to entity
        val user = User(userToRegister.username, userToRegister.password, userToRegister.email)

        val validationStatus = isValidUser(user)
        if(validationStatus != ValidationStatus.VALID)
            return RegisterDTO(validationStatus, null)

        // If the user is valid then save their data in the DB
        // However the registration is not complete until activation with code
        user.salt = BCrypt.gensalt(10)
        user.password = BCrypt.hashpw(user.password, user.salt)
        val savedUser = userRepository.save(user)
        val activationDTO = emailService.insertActivation(savedUser)

        return RegisterDTO(validationStatus, activationDTO)
    }

    fun isValidUser(user: User): ValidationStatus {
        user.username.isNotBlank() || return ValidationStatus.NO_USERNAME
        user.email.isNotBlank() || return ValidationStatus.NO_EMAIL
        user.password!!.isNotBlank() || return ValidationStatus.NO_PASSWORD
        user.password!!.matches(passwordRegex) || return ValidationStatus.WEAK_PASSWORD
        user.email.matches(emailRegex) || return ValidationStatus.INVALID_EMAIL

        // TODO("Check is username and email are System Unique")

        return ValidationStatus.VALID
    }
}
