package ch.taskify.config

import ch.taskify.service.security.LoginAttemptService
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class AuthenticationAuditListener(
    private val loginAttemptService: LoginAttemptService
) {

    @EventListener
    fun onFailure(event: AuthenticationFailureBadCredentialsEvent) {
        loginAttemptService.recordFailure(event.authentication.name ?: "")
    }

    @EventListener
    fun onSuccess(event: AuthenticationSuccessEvent) {
        val username = when (val principal = event.authentication.principal) {
            is UserDetails -> principal.username
            else -> event.authentication.name
        }

        loginAttemptService.recordSuccess(username ?: "")
    }
}
