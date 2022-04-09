package it.polito.wa2.group03userregistration.integration

import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import java.util.*

// This attribute recreates the WebServer before each test, this is a simple-yet-ugly
// workaround to re-initialize the already checked ticket list before each test
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateLimiterTest {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Normal request flow`() {
        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(ActivationDTO(UUID.randomUUID(), "test-email", "123"))

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/users/validate",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Too many requests after 10 requests`() {
        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(ActivationDTO(UUID.randomUUID(), "test-email", "123"))
        for (i in (1..10)) {
            restTemplate.postForEntity<String>(
                "$baseUrl/users/validate",
                request
            )
        }

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/users/validate",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.statusCode)
    }

    @Test
    fun `10 valid requests allowed`() {
        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(ActivationDTO(UUID.randomUUID(), "test-email", "123"))
        val responses = mutableListOf<HttpStatus>()

        // Act
        for (i in (1..10)) {
            responses.add(
                restTemplate.postForEntity<String>(
                    "$baseUrl/users/validate",
                    request
                ).statusCode
            )
        }

        // Assert
        Assertions.assertEquals(10, responses.count { it == HttpStatus.NOT_FOUND })
    }
}