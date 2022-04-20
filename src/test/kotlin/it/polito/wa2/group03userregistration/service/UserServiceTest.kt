package it.polito.wa2.group03userregistration.service

import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import it.polito.wa2.group03userregistration.dtos.toDTO
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.ActivationStatus
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.repositories.UserRepository
import it.polito.wa2.group03userregistration.services.EmailServiceStub
import it.polito.wa2.group03userregistration.services.UserService
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
import java.util.*

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

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var emailServiceStub: EmailServiceStub

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

        /** user with an existing email and then user with an existing username */
        userRepository.save(validUser)
        val duplicateUsername = User(username, psw, "anotherone@maildomain.invalid")
        val duplicateEmail = User("another_username", psw, email)
        Assertions.assertEquals(
            UserValidationStatus.USERNAME_ALREADY_EXISTS,
            userService.isValidUser(duplicateUsername)
        )
        Assertions.assertEquals(UserValidationStatus.EMAIL_ALREADY_EXISTS, userService.isValidUser(duplicateEmail))

    }

    @Test
    fun testValidateUser() {

        val username = "user2"
        val psw = "P4ssw0rd!"
        val email = "user2@maildomain.invalid"
        val user = User(username, psw, email)
        val wrongCode = "code1"
        val randomUUID = UUID.randomUUID()

        /** first search for an id which does not exist, as the activation was not inserted */
        val wrongIdDTO = ActivationDTO(randomUUID, email, wrongCode)
        val resWrongId = userService.validateUser(wrongIdDTO)
        Assertions.assertEquals(ActivationStatus.ID_DOES_NOT_EXIST, resWrongId.status)
        Assertions.assertNull(resWrongId.user)

        val savedUser = userRepository.save(user)
        val savedActivationDTO = emailServiceStub.insertActivation(savedUser)

        /**
         * test the wrong activation code. the expired timestamp case
         * would require an extra stub for the activation, so we skip it.
         */
        val wrongCodeDTO = savedActivationDTO?.let { ActivationDTO(it.provisionalId, email, wrongCode) }
        val resWrongCode = wrongCodeDTO?.let { userService.validateUser(it) }
        if (resWrongCode != null) {
            Assertions.assertEquals(ActivationStatus.WRONG_ACTIVATION_CODE, resWrongCode.status)
            Assertions.assertNull(resWrongCode.user)
        } else {
            /** if something goes wrong the assertion should fail and not be skipped */
            Assertions.assertTrue(false)
        }

        /**
         * test a successful validation, and then run it again as it should
         * not validate twice the same user-code couple.
         */
        val res = savedActivationDTO?.let { userService.validateUser(it) }
        if (res != null) {
            Assertions.assertEquals(ActivationStatus.SUCCESSFUL, res.status)
            Assertions.assertEquals(username, res.user?.username)
        }
        val resSecondTry = savedActivationDTO?.let { userService.validateUser(it) }
        if (resSecondTry != null)
            Assertions.assertEquals(ActivationStatus.ID_DOES_NOT_EXIST, resSecondTry.status)

    }

    @Test
    fun testRegisterUser() {

        val username = "user3"
        val psw = "P4ssw0rd!"
        val email = "user3@maildomain.invalid"

        /**
         * we only test the successful case and one generic fail as all the
         * other cases have been covered in previous tests.
         */
        val invalidUser = User("", psw, email).toDTO()
        val validUser = User(username, psw, email).toDTO()
        Assertions.assertNotEquals(UserValidationStatus.VALID, userService.registerUser(invalidUser).status)
        Assertions.assertEquals(UserValidationStatus.VALID, userService.registerUser(validUser).status)

    }

}
