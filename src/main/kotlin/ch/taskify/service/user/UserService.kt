package ch.taskify.service.user

import ch.taskify.dto.UserDTO
import ch.taskify.entity.user.Role
import ch.taskify.entity.user.UserEntity
import java.util.UUID

interface UserService {
    fun findAll(): List<UserDTO>

    fun findById(id: UUID): UserDTO?

    fun findByUsername(username: String): UserDTO?

    fun createUser(
        username: String,
        rawPassword: String,
        role: Role = Role.USER
    ): UserDTO

    fun updateUser(
        id: UUID,
        username: String? = null,
        rawPassword: String? = null,
        role: Role? = null
    ): UserDTO

    fun deleteUser(id: UUID)
}
