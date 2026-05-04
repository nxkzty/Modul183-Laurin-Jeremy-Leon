package ch.taskify.service.User

import ch.taskify.dto.SignUpDTO
import ch.taskify.entity.user.UserEntity
import ch.taskify.repository.UserRepository
import jakarta.persistence.EntityExistsException
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

/*
 * UserService.java  
 *
 * Creator:
 * 04.05.2026 11:12 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 11:12 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

@Service
@Validated
class UserService(private val jpa: UserRepository,
                  private val pwEncoder: PasswordEncoder,) {

    fun signUp(@Valid req: SignUpDTO) {
        if (jpa.existsByNameIgnoreCase(req.username)) {
            throw EntityExistsException("A user with the name ${req.username} already exists.")
        }

        val newUser = UserEntity().apply {
            name = req.username
            passwordHash = pwEncoder.encode(req.password)!!
        }

        jpa.save(newUser)
    }

    fun getProfile(): String {
        val user = SecurityContextHolder.getContext().authentication!!.principal as UserEntity
        return user.name
    }

}