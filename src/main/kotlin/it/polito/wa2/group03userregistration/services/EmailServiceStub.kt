package it.polito.wa2.group03userregistration.services

import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service

@Service
class EmailServiceStub : EmailService() {

    private var sentMails: MutableList<SimpleMailMessage> = mutableListOf()

    override fun sendMail(message: SimpleMailMessage) {
        sentMails.add(message)
    }

    fun getSentMails(): MutableList<SimpleMailMessage> {
        return sentMails
    }

    fun getSentMailsSize(): Int {
        return sentMails.size
    }

}
