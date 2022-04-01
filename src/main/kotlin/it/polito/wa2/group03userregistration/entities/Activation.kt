package it.polito.wa2.group03userregistration.entities

import java.util.UUID
import javax.persistence.*

@Entity
class Activation {

    @Id
    @GeneratedValue
    lateinit var id: UUID

    @OneToOne
    lateinit var userActivation: User

    var activationCode: String? = null

    var attempt: Int = 5

}
