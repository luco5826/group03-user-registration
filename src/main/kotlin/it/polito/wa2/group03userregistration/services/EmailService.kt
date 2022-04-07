package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import it.polito.wa2.group03userregistration.dtos.DTO
import it.polito.wa2.group03userregistration.entities.Activation
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.repositories.ActivationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class EmailService{

    private val ACTIVATIONCODE_LENGTH = 10
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')+('0'..'9')
    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Autowired
    lateinit var mailSender: JavaMailSender;

    fun insertActivation(user: User): ActivationDTO? {
        var savedEntity: Activation? = null

        try {
            savedEntity = activationRepository.save(Activation(user, generateActivationCode()))
            sendMail(savedEntity.userActivation.email, savedEntity.userActivation.username,savedEntity.activationCode, savedEntity.expirationDate!!)
        } catch (e: MailException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return savedEntity?.DTO()
    }

    fun generateActivationCode(): String {
        return (1..ACTIVATIONCODE_LENGTH)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map { charPool[it] }
            .joinToString("")
    }

    fun sendMail(toEmail: String, username: String, activationCode: String, expirationDate: Date){
        val message = SimpleMailMessage()
        message.setFrom("group03NML@gmail.com")
        message.setTo(toEmail)
        message.setText("Hello $username! This is your activation code $activationCode. Please use it before ${SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(expirationDate)}.\nHave a nice day!")
        message.setSubject("Activation code")

        mailSender.send(message)
    }

}