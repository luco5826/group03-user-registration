package it.polito.wa2.group03userregistration.entities

import java.time.LocalDateTime
import java.util.Date
import java.util.UUID
import javax.persistence.*

@Entity
class Activation(@OneToOne var userActivation: User, var activationCode: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null


    @Temporal(TemporalType.TIMESTAMP)
    var expirationDate: Date? = java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(6))

    var attempt: Int = 5
}
