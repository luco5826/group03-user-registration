package it.polito.wa2.group03userregistration.entities

import org.hibernate.annotations.CreationTimestamp
import java.util.Date
import java.util.UUID
import javax.persistence.*

@Entity
class Activation(@OneToOne var userActivation: User, var activationCode: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    lateinit var expirationDate: Date

    var attempt: Int = 5
}
