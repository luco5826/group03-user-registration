package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.repositories.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class UserDTOTests {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
        }
    }

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun testConstructor() {
        val userDto = UserDTO(1, "user1", "user1@email.com", "psw1")
        Assertions.assertEquals(userDto.userId, 1)
        Assertions.assertEquals(userDto.username, "user1")
        Assertions.assertEquals(userDto.email, "user1@email.com")
        Assertions.assertEquals(userDto.password, "psw1")
    }

    @Test
    fun testToDTO() {
        val user = User("user1", "psw1", "user1@email.com")
        val savedUser = userRepository.save(user)
        val userDto = UserDTO(savedUser.id, "user1", "user1@email.com", "psw1")
        Assertions.assertEquals(savedUser.toDTO(), userDto)
    }

    @Test
    fun testToDTOWrongUsername() {
        val user = User("user1", "psw1", "user1@email.com")
        val savedUser = userRepository.save(user)
        val userDto = UserDTO(savedUser.id, "another_user", "user1@email.com", "psw1")
        Assertions.assertNotEquals(savedUser.toDTO(), userDto)
    }

    @Test
    fun testToDTOWrongEmail() {
        val user = User("user1", "psw1", "user1@email.com")
        val savedUser = userRepository.save(user)
        val userDto = UserDTO(savedUser.id, "user1", "another_user@email.com", "psw1")
        Assertions.assertNotEquals(savedUser.toDTO(), userDto)
    }

    @Test
    fun testToDTOChangePassword() {
        val user = User("user1", null, "user1@email.com")
        Assertions.assertNull(user.toDTO().password)
        user.password = "brand_new_psw"
        Assertions.assertEquals(user.toDTO().password, "brand_new_psw")
    }

}
