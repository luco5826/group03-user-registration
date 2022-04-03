package it.polito.wa2.group03userregistration.services

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
    lateinit var emailService: EmailService;

    @Autowired
    lateinit var userRepository: UserRepository

    fun registerUser(userToRegister: UserDTO): ValidationUser {
        var user = User(userToRegister.username, userToRegister.password, userToRegister.email)
        if(!isValidUser(user)) return ValidationUser.USER_NOT_VALID
        user.salt = BCrypt.gensalt(10)
        user.password = BCrypt.hashpw(user.password, user.salt)
        val savedUser = userRepository.save(user)
        val activationDTO = emailService.insertActivation(savedUser);
        println(activationDTO)
        return ValidationUser.VALID
    }

    fun isValidUser(user: User): Boolean {
        // TODO("Check is username and email are System Unique")
        return  user.username.isNotBlank() &&
                user.email.isNotBlank() &&
                user.password!!.isNotBlank() &&
                user.password!!.matches(passwordRegex) &&
                user.email.matches(emailRegex)
    }
}