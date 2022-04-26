package it.polito.wa2.group03userregistration.entities

import it.polito.wa2.group03userregistration.enums.UserRole
import javax.persistence.*

@Entity
@Table(name = "ApplicationUser")
class User(var username: String, var password: String?, var email: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    var salt: String = ""

    var role: UserRole = UserRole.CUSTOMER

    @OneToOne(mappedBy = "userActivation")
    var activation: Activation? = null

    var enabled = 0
}
