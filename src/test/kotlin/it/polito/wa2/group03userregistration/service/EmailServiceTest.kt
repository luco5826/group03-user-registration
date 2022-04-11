package it.polito.wa2.group03userregistration.service

import it.polito.wa2.group03userregistration.services.EmailService
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
import java.text.SimpleDateFormat
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmailServiceTest {

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
    lateinit var emailService: EmailService

    @Test
    fun testActivationCodeGeneration() {
        /**
         * in this implementation an activation code has an expected length of 10
         * plus it must be made of letters and numbers only. generate and check 20
         * different codes to be sure.
         */
        repeat(20) {
            Assertions.assertTrue(
                "^[a-zA-Z0-9]{10}$".toRegex()
                    .matches(emailService.generateActivationCode())
            )
        }
    }

    @Test
    fun testGenerateMail() {
        val date = Date()
        val simpleDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date)
        val message = emailService.generateMail(
            "destination@mail_provider.invalid",
            "user1",
            "act1vat1on",
            date
        )
        val text = "Hello user1! This is your activation code:\n\nact1vat1on \n\nPlease use it before ${simpleDate}.\nHave a nice day!"
        /**
         * "subject" and "from" field are hardcoded, while "text" is generated following the above syntax
         * and the "to" field is passed as parameter.
         */
        Assertions.assertEquals("Activation code", message.subject)
        Assertions.assertEquals("group03NML@gmail.com", message.from)
        Assertions.assertEquals("destination@mail_provider.invalid", message.to?.get(0) ?: "FAIL - no destination specified.")
        Assertions.assertEquals(text, message.text)
    }

}