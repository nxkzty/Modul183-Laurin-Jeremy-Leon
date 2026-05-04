package ch.taskify.service.user

import ch.taskify.entity.user.Role
import ch.taskify.entity.user.UserEntity
import java.util.UUID

interface UserService {
    fun findAll(): List<UserEntity>

    fun findById(id: UUID): UserEntity?

    fun findByUsername(username: String): UserEntity?

    fun createUser(
        username: String,
        rawPassword: String,
        role: Role = Role.USER
    ): UserEntity

    fun updateUser(
        id: UUID,
        username: String? = null,
        rawPassword: String? = null,
        role: Role? = null
    ): UserEntity

    fun deleteUser(id: UUID)
}
