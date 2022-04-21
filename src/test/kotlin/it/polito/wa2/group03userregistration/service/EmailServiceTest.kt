package it.polito.wa2.group03userregistration.service

import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.repositories.UserRepository
import it.polito.wa2.group03userregistration.services.EmailService
import it.polito.wa2.group03userregistration.services.EmailServiceStub
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

    @Autowired
    lateinit var emailServiceStub: EmailServiceStub

    @Autowired
    lateinit var userRepository: UserRepository

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
        val username = "user1"
        val email = "user1@maildomain.invalid"
        val code = "act1vat1on"
        val message = emailService.generateMail(email, username, code, date)
        val text =
            "Hello user1! This is your activation code:\n\nact1vat1on \n\nPlease use it before ${simpleDate}.\nHave a nice day!"
        /**
         * "subject" and "from" field are hardcoded, while "text" is generated following the above syntax
         * and the "to" field is passed as parameter.
         */
        Assertions.assertEquals("Activation code", message.subject)
        Assertions.assertEquals("group03NML@gmail.com", message.from)
        Assertions.assertEquals(email, message.to?.get(0))
        Assertions.assertEquals(text, message.text)

    }

    @Test
    fun testInsertActivation() {

        val username = "user1"
        val psw = "P4ssw0rd!"
        val email = "user1@maildomain.invalid"
        val user = User(username, psw, email)
        val sentMailNo = emailServiceStub.getSentMailsSize()

        /**
         * since we actually need an existing user to avoid hibernate
         * errors, this is more of an integration test. using the repository
         * instead of UserService is an attempt to decouple as much as
         * possible the test from the implementation.
         */
        val savedUser = userRepository.save(user)

        /**
         * the stub inherits the insertActivation() method from the actual
         * service, so it's fine to test it this way. the only change is that
         * the email is not actually sent, but it is instead added to a list
         * in memory that we can use to check the sending phase was triggered.
         */
        val savedActivationDTO = emailServiceStub.insertActivation(savedUser)
        val activationDTO =
            savedActivationDTO?.let { ActivationDTO(it.provisionalId, email, it.activationCode) }
        Assertions.assertEquals(activationDTO, savedActivationDTO)
        Assertions.assertEquals(sentMailNo + 1, emailServiceStub.getSentMailsSize())

        /**
         * while we are at it let's also test that it's the message we expect
         * being sent to the correct address. the overall formatting of the
         * message is already tested elsewhere.
         */
        val message = emailServiceStub.getSentMails()[0]
        Assertions.assertEquals("Activation code", message.subject)
        Assertions.assertEquals(email, message.to?.get(0))

    }

}
