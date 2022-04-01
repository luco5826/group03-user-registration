package it.polito.wa2.group03userregistration.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="ApplicationUser")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null;

    var username: String = ""
    var password: String = ""
    var salt: String = ""
    var email: String = ""
}