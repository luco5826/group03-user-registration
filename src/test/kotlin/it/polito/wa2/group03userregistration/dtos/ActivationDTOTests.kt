package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.entities.Activation
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.repositories.ActivationRepository
import it.polito.wa2.group03userregistration.repositories.UserRepository
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
class ActivationDTOTests {

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
    lateinit var activationRepository: ActivationRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun testConstructor() {
        val uuid = UUID.randomUUID()
        val activationDTO = ActivationDTO(uuid, "user1@email.com", "code1")
        Assertions.assertEquals(uuid, activationDTO.provisionalId)
        Assertions.assertEquals("user1@email.com", activationDTO.email)
        Assertions.assertEquals("code1", activationDTO.activationCode)
    }

    @Test
    fun testToDTO() {
        val user = User("user1", "psw1", "user1@email.com")
        val savedUser = userRepository.save(user)
        val activation = Activation(savedUser, "code1")
        val savedActivation = activationRepository.save(activation)
        val activationDTO = savedActivation.id?.let { ActivationDTO(it, "user1@email.com", "code1") }
        Assertions.assertEquals(activationDTO, savedActivation.toDTO())
    }

    @Test
    fun testToDTOWrongEmail() {
        val user = User("user1", "psw1", "user1@email.com")
        val savedUser = userRepository.save(user)
        val activation = Activation(savedUser, "code1")
        val savedActivation = activationRepository.save(activation)
        val activationDTO = savedActivation.id?.let { ActivationDTO(it, "another_user@email.com", "code1") }
        Assertions.assertNotEquals(activationDTO, savedActivation.toDTO())
    }

    @Test
    fun testToDTOWrongCode() {
        val user = User("user1", "psw1", "user1@email.com")
        val savedUser = userRepository.save(user)
        val activation = Activation(savedUser, "code1")
        val savedActivation = activationRepository.save(activation)
        val activationDTO = savedActivation.id?.let { ActivationDTO(it, "user1@email.com", "completely_wrong_code") }
        Assertions.assertNotEquals(activationDTO, savedActivation.toDTO())
    }

    @Test
    fun testToDTORandomId() {
        val uuid = UUID.randomUUID()
        val user = User("user1", "psw1", "user1@email.com")
        val savedUser = userRepository.save(user)
        val activation = Activation(savedUser, "code1")
        val savedActivation = activationRepository.save(activation)
        val activationDTO = ActivationDTO(uuid, "user1@email.com", "code1")
        Assertions.assertNotEquals(activationDTO, savedActivation.toDTO())
    }

}