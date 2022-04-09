package it.polito.wa2.group03userregistration.dtos

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class ActivationDTOTests {

    @Test
    fun testConstructor() {
        val uuid = UUID.randomUUID()
        val activationDTO = ActivationDTO(uuid,  "user1@email.com", "code1")
        Assertions.assertEquals(activationDTO.provisionalId, uuid)
        Assertions.assertEquals(activationDTO.email, "user1@email.com")
        Assertions.assertEquals(activationDTO.activationCode, "code1")
    }

}