package it.polito.wa2.group03userregistration.repositories

import it.polito.wa2.group03userregistration.entities.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository: CrudRepository<User, Long> {}
