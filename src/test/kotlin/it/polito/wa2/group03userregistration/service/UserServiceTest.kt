package it.polito.wa2.group03userregistration.service

import it.polito.wa2.group03userregistration.dtos.UserDTO
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.services.UserService
import org.junit.Ignore
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var userService: UserService

    @Test
    fun testIsValidUser() {

        val username = "user1"
        val psw = "P4ssw0rd!"
        val email = "user1@maildomain.invalid"

        /** a valid user */
        val validUser = User(username, psw, email)
        Assertions.assertEquals(UserValidationStatus.VALID, userService.isValidUser(validUser))

        /** user with blank username */
        val blankUsername = User("", psw, email)
        Assertions.assertEquals(UserValidationStatus.NO_USERNAME, userService.isValidUser(blankUsername))

        /** user with blank email and broken email */
        val blankEmail = User(username, psw, "")
        val wrongEmail = User(username, psw, "wrong-looking-email")
        Assertions.assertEquals(UserValidationStatus.NO_EMAIL, userService.isValidUser(blankEmail))
        Assertions.assertEquals(UserValidationStatus.INVALID_EMAIL, userService.isValidUser(wrongEmail))

        /** user with blank password and weak password */
        val blankPassword = User(username, "", email)
        val weakPassword = User(username, "ab12", email)
        Assertions.assertEquals(UserValidationStatus.NO_PASSWORD, userService.isValidUser(blankPassword))
        Assertions.assertEquals(UserValidationStatus.WEAK_PASSWORD, userService.isValidUser(weakPassword))

    }

    @Ignore
    @Test
    fun insertValidUser() {
        val user = UserDTO(null, "alex142", "alessandrobacci142@gmail.com", "Pass!w0rd")
        Assertions.assertEquals(UserValidationStatus.VALID, userService.registerUser(user).status)
    }

}
