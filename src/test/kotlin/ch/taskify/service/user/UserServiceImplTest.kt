package ch.taskify.service.user

import ch.taskify.entity.user.Role
import ch.taskify.entity.user.UserEntity
import ch.taskify.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import java.util.UUID

class UserServiceImplTest {

    private val userRepository = mock(UserRepository::class.java)
    private val passwordEncoder = mock(PasswordEncoder::class.java)
    private val userService = UserServiceImpl(userRepository, passwordEncoder)

    @Test
    fun `createUser hashes password and saves normalized username`() {
        `when`(userRepository.existsByNameIgnoreCase("admin")).thenReturn(false)
        `when`(passwordEncoder.encode("123")).thenReturn("hashed-123")
        `when`(userRepository.save(any(UserEntity::class.java))).thenAnswer { it.arguments[0] }

        val user = userService.createUser("  admin  ", "123", Role.ADMIN)

        assertEquals("admin", user.name)
        assertEquals("hashed-123", user.passwordHash)
        assertEquals(Role.ADMIN, user.role)
        verify(userRepository).save(any(UserEntity::class.java))
    }

    @Test
    fun `createUser rejects duplicate usernames`() {
        `when`(userRepository.existsByNameIgnoreCase("admin")).thenReturn(true)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.createUser("admin", "123")
        }

        assertTrue(exception.message!!.contains("already exists"))
        verify(userRepository, never()).save(any(UserEntity::class.java))
    }

    @Test
    fun `updateUser changes username password and role`() {
        val userId = UUID.randomUUID()
        val existingUser = UserEntity().apply {
            id = userId
            name = "admin"
            passwordHash = "old-hash"
            role = Role.USER
        }

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByNameIgnoreCase("new-admin")).thenReturn(null)
        `when`(passwordEncoder.encode("456")).thenReturn("hashed-456")
        `when`(userRepository.save(existingUser)).thenReturn(existingUser)

        val updatedUser = userService.updateUser(
            id = userId,
            username = "  new-admin  ",
            rawPassword = "456",
            role = Role.ADMIN
        )

        assertEquals("new-admin", updatedUser.name)
        assertEquals("hashed-456", updatedUser.passwordHash)
        assertEquals(Role.ADMIN, updatedUser.role)
    }

    @Test
    fun `deleteUser removes existing user`() {
        val userId = UUID.randomUUID()
        `when`(userRepository.existsById(userId)).thenReturn(true)
        doNothing().`when`(userRepository).deleteById(userId)

        userService.deleteUser(userId)

        verify(userRepository).deleteById(userId)
    }

    @Test
    fun `findByUsername trims username before lookup`() {
        val user = UserEntity().apply { name = "admin" }
        `when`(userRepository.findByNameIgnoreCase("admin")).thenReturn(user)

        val foundUser = userService.findByUsername("admin")

        assertNotNull(foundUser)
        assertEquals("admin", foundUser?.name)
    }
}
