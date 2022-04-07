package it.polito.wa2.group03userregistration.controllers

import it.polito.wa2.group03userregistration.dtos.UserDTO
import it.polito.wa2.group03userregistration.services.UserService
import it.polito.wa2.group03userregistration.services.ValidationMessages
import it.polito.wa2.group03userregistration.services.ValidationStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

interface RegisterResponse
data class RegisterResponseOK (val provisional_id: UUID,
                               val email: String): RegisterResponse

data class RegisterResponseError (val errorMessage: String?): RegisterResponse

@RestController
class WebController {
    @Autowired
    lateinit var userService: UserService

    @PostMapping("/user/register")
    fun registerUser(@RequestBody payload: UserDTO): ResponseEntity<RegisterResponse> {
        val registerDTO = userService.registerUser(payload)
        val resBody: RegisterResponse

        return if (registerDTO.status == ValidationStatus.VALID) {
            resBody = RegisterResponseOK(registerDTO.activation!!.provisionalId, registerDTO.activation.email!!)
            ResponseEntity.status(HttpStatus.ACCEPTED).body(resBody)
        } else {
            resBody = RegisterResponseError(ValidationMessages[registerDTO.status])
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody)
        }
    }
}
