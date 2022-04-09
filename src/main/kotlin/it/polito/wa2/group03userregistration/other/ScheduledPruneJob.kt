package it.polito.wa2.group03userregistration.other

import it.polito.wa2.group03userregistration.repositories.ActivationRepository
import it.polito.wa2.group03userregistration.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

@EnableScheduling
@Configuration
class ScheduledPruneJob {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Scheduled(initialDelayString = "PT01M", fixedDelayString = "PT01M")
    fun checkExpiredRegistrationToken() {
        val expiredRecords = activationRepository.findAll().filter {
            it.expirationDate!!.before(
                java.sql.Timestamp.valueOf(
                    LocalDateTime.now()
                )
            )
        }
        expiredRecords.forEach {
            activationRepository.deleteById(it.id!!)
            userRepository.deleteById(it.userActivation.id!!)
        }
    }
}