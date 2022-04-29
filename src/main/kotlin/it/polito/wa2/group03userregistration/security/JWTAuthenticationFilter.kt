package it.polito.wa2.group03userregistration.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group03userregistration.dtos.LoginResponse
import it.polito.wa2.group03userregistration.dtos.UserLoginDTO
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.InputStream
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(authenticationManager: AuthenticationManager, private val secret: String) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {

    init {
        setFilterProcessesUrl("/user/login")
    }


    private val JWT_TOKEN_VALIDITY = 5 * 60 * 60

    @kotlin.jvm.Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        return try {
            val mapper = jacksonObjectMapper()
            val creds = mapper.readValue<UserLoginDTO>(request?.inputStream as InputStream)

            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    creds.username,
                    creds.password,
                    ArrayList()
                )
            )
        } catch (e: AuthenticationException) {
            throw AuthenticationServiceException(e.message)
        }

    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        failed: AuthenticationException?
    ) {
        response?.status = HttpServletResponse.SC_NOT_FOUND
        response?.writer?.write(failed?.message ?: "Error")
        response?.writer?.flush()
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val authClaims: MutableList<String> = mutableListOf()
        authResult?.authorities?.let { authorities ->
            authorities.forEach { claim -> authClaims.add(claim.toString()) }
        }
        val principal = authResult?.principal as UserPrincipal
        val roles: List<String> = principal.authorities.map { it.authority.toString() }
        val token = Jwts.builder()
            .setSubject(principal.username)
            .claim("auth", authClaims)
            .claim("roles", roles)
            .setSubject("login-auth")
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
            .signWith(Keys.hmacShaKeyFor(secret.toByteArray()), SignatureAlgorithm.HS512)
            .compact()
        response?.addHeader("Authorization", "Bearer $token")

        // Prepare JSON response with username and token generated
        val body = LoginResponse(principal.username, token)
        val jsonBody = jacksonObjectMapper().writeValueAsString(body)
        response?.contentType = "application/json"
        response?.status = HttpServletResponse.SC_OK
        response?.writer?.write(jsonBody)
        response?.writer?.flush()
    }
}