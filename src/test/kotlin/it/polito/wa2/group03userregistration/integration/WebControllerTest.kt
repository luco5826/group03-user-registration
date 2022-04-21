package it.polito.wa2.group03userregistration.integration

import com.dumbster.smtp.SimpleSmtpServer
import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import it.polito.wa2.group03userregistration.dtos.UserDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JsonParserFactory
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*


// This attribute recreates the WebServer before each test, this is a simple-yet-ugly
// workaround to re-initialize the already checked ticket list before each test
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WebControllerTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        val dumbster: SimpleSmtpServer = SimpleSmtpServer.start(SimpleSmtpServer.AUTO_SMTP_PORT)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.mail.port", dumbster::getPort)
            registry.add("spring.mail.host") { "localhost" }
        }
    }

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate


    @BeforeEach
    fun clearMails() {
        dumbster.reset()
    }

    @Test
    fun `Normal request flow`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "me@email.com", "Asd123!asdasd"))

        // Act
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)
        Assertions.assertEquals(1, dumbster.receivedEmails.size)
    }

    @Test
    fun `Wrong email format`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "wrong-email.it", "Asd123!asdasd"))

        // Act
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Weak password`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "me@email.com", "123"))

        // Act
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Missing username field`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "", "me@email.com", "Password1234!"))

        // Act
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Missing email field`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "", "Password123!"))

        // Act
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Missing password field`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "me@email.com", null))

        // Act
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Validation correct`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "me@email.com", "Password1234!"))
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )
        val parser = JsonParserFactory.getJsonParser()
        val provisionalId = parser.parseMap(response.body)["provisional_id"]
        val email = parser.parseMap(response.body)["email"]
        val activationCode = dumbster.receivedEmails[0].body.split("code:")[1].split(" ")[0].trim()

        // Act
        val activationRequest = HttpEntity(ActivationDTO(UUID.fromString(provisionalId as String), email as String, activationCode))
        val activationResponse = restTemplate.postForEntity<String>(
                "$baseUrl/users/validate",
                activationRequest
        )

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, activationResponse.statusCode)
    }

    @Test
    fun `Validation wrong code`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "me@email.com", "Password1234!"))
        val response = restTemplate.postForEntity<String>(
                "$baseUrl/users/register",
                request
        )
        val parser = JsonParserFactory.getJsonParser()
        val provisionalId = parser.parseMap(response.body)["provisional_id"]
        val email = parser.parseMap(response.body)["email"]
        val activationCode = "fake code"

        // Act
        val activationRequest = HttpEntity(ActivationDTO(UUID.fromString(provisionalId as String), email as String, activationCode))
        val activationResponse = restTemplate.postForEntity<String>(
                "$baseUrl/users/validate",
                activationRequest
        )

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, activationResponse.statusCode)
    }
}