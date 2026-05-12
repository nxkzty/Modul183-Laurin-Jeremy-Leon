package ch.taskify.service.user

import ch.taskify.entity.user.UserEntity
import ch.taskify.repository.UserRepository
import ch.taskify.service.security.LoginAttemptService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.core.userdetails.UsernameNotFoundException

class UserDetailsServiceImplTest {

    private val userRepository = mock(UserRepository::class.java)
    private val loginAttemptService = mock(LoginAttemptService::class.java)
    private val userDetailsService = UserDetailsServiceImpl(userRepository, loginAttemptService)

    @Test
    fun `loadUserByUsername returns trimmed username match`() {
        val user = UserEntity().apply { name = "admin" }
        `when`(userRepository.findByNameIgnoreCase("admin")).thenReturn(user)

        val foundUser = userDetailsService.loadUserByUsername("  admin  ")

        assertEquals("admin", foundUser.username)
    }

    @Test
    fun `loadUserByUsername throws when user is missing`() {
        `when`(userRepository.findByNameIgnoreCase("admin")).thenReturn(null)

        assertThrows(UsernameNotFoundException::class.java) {
            userDetailsService.loadUserByUsername("admin")
        }
    }
}
