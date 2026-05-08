package ch.taskify.repository

import ch.taskify.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByNameIgnoreCase(username: String): UserEntity?
    fun existsByNameIgnoreCase(username: String): Boolean
}
