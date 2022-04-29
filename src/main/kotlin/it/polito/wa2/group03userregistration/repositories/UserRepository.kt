package it.polito.wa2.group03userregistration.repositories

import it.polito.wa2.group03userregistration.entities.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
interface UserRepository : CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    fun findByUsername(username: String): User?

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.enabled = 1 WHERE u.email = ?1")
    fun enableUserByEmail(email: String): Unit

}
