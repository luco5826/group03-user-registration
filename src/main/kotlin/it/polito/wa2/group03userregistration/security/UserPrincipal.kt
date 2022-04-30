package it.polito.wa2.group03userregistration.security

import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.UserRolesMapping
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(private val user: User): UserDetails {
    override fun getPassword(): String {
        return user.password!!
    }

    override fun getUsername(): String {
       return user.id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return user.enabled == 1
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
       return mutableListOf(GrantedAuthority { UserRolesMapping[user.role] })
    }
}