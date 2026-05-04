package ch.taskify.service

import ch.taskify.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/*
 * UserDetailService.java  
 *
 * Creator:
 * 04.05.2026 08:17 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 08:17 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */
class UserDetailsServiceImpl(
    private val userJpa: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userJpa.findByNameIgnoreCase(username)
            ?: throw UsernameNotFoundException("Could not find user with username $username")
        return user
    }
}
