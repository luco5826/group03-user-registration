package it.polito.wa2.group03userregistration.repositories

import it.polito.wa2.group03userregistration.entities.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    fun findByUsername(username: String): User?

}
