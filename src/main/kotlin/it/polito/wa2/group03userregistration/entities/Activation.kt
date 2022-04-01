package it.polito.wa2.group03userregistration.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity
class Activation {
    @Id
    var id: UUID? = null

    @OneToOne
    val userActivation: User? = null

    var attempt: Int = 5
}