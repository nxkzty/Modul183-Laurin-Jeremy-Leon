package ch.taskify.utils

import ch.taskify.dto.UserDTO
import ch.taskify.entity.user.UserEntity
import org.springframework.security.core.context.SecurityContextHolder

object CurrentUser {
    val name: String
        get() = SecurityContextHolder.getContext().authentication?.name ?: "Unknown"
    val principalAsUserEntity: UserEntity?
        get() = SecurityContextHolder.getContext().authentication?.principal as? UserEntity
}