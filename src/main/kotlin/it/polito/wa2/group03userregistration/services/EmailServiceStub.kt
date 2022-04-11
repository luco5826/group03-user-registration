package it.polito.wa2.group03userregistration.services

import org.springframework.mail.SimpleMailMessage

class EmailServiceStub : EmailService() {

    private lateinit var sentMails: MutableList<SimpleMailMessage>

    override fun sendMail(message: SimpleMailMessage) {
        sentMails.add(message)
    }

}
