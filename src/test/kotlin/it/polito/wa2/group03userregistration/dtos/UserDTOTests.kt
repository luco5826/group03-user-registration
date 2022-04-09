package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.entities.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserDTOTests {

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
        val user = User("user1", null, "user1@email.com")
        val userDto = UserDTO(null, "user1", "user1@email.com", null)
        Assertions.assertEquals(user.toUserDTO(), userDto)
    }

    @Test
    fun testToDTOWrongUsername() {
        val user = User("user1", null, "user1@email.com")
        val userDto = UserDTO(null, "another_user", "user1@email.com", null)
        Assertions.assertNotEquals(user.toUserDTO(), userDto)
    }

    @Test
    fun testToDTOWrongEmail() {
        val user = User("user1", null, "user1@email.com")
        val userDto = UserDTO(null, "user1", "another_user@email.com", null)
        Assertions.assertNotEquals(user.toUserDTO(), userDto)
    }

    @Test
    fun testToDTOWrongPassword() {
        val user = User("user1", null, "user1@email.com")
        Assertions.assertNull(user.toUserDTO().password)
        user.password = "psw1"
        Assertions.assertEquals(user.toUserDTO().password, "psw1")
    }

}
