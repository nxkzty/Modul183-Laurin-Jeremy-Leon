package ch.taskify.service.user

import ch.taskify.repository.UserRepository
import ch.taskify.service.security.LoginAttemptService
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val loginAttemptService: LoginAttemptService
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        if (loginAttemptService.isLocked(username)) {
            throw LockedException("User is temporarily locked")
        }

        val user = userRepository.findByNameIgnoreCase(username.trim())
            ?: throw UsernameNotFoundException("Could not find user with username $username")
        return user
    }
}
