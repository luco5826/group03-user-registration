package it.polito.wa2.group03userregistration.service

import it.polito.wa2.group03userregistration.dtos.UserDTO
import it.polito.wa2.group03userregistration.services.UserService
import it.polito.wa2.group03userregistration.services.ValidationUser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    @Test
    fun insertValidUser() {
        val user = UserDTO(null,  "alex142", "alessandrobacci142@gmail.com", "Pass!w0rd")
        Assertions.assertEquals(ValidationUser.VALID, userService.registerUser(user))
    }

}