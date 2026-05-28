package ch.taskify.service.security

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class LoginAttemptService {

    private val log = LoggerFactory.getLogger(LoginAttemptService::class.java)
    private val attempts = ConcurrentHashMap<String, AttemptState>()

    private val maxAttempts = 5
    private val lockDuration = Duration.ofMinutes(10)

    fun isLocked(username: String): Boolean {
        val state = attempts[normalize(username)] ?: return false
        val lockedUntil = state.lockedUntil ?: return false

        if (Instant.now().isAfter(lockedUntil)) {
            attempts.remove(normalize(username))
            return false
        }

        return true
    }

    fun recordFailure(username: String) {
        val normalizedUsername = normalize(username)
        val state = attempts.compute(normalizedUsername) { _, current ->
            val failedAttempts = (current?.failedAttempts ?: 0) + 1
            val lockedUntil = if (failedAttempts >= maxAttempts) {
                Instant.now().plus(lockDuration)
            } else {
                null
            }

            AttemptState(failedAttempts, lockedUntil)
        } ?: return

        if (state.lockedUntil != null) {
            log.warn("Login temporarily locked: username={}, failedAttempts={}", normalizedUsername, state.failedAttempts)
        } else {
            log.warn("Login failed: username={}, failedAttempts={}", normalizedUsername, state.failedAttempts)
        }
    }

    fun recordSuccess(username: String) {
        attempts.remove(normalize(username))
        log.info("Login successful: username={}", normalize(username))
    }

    private fun normalize(username: String): String = username.trim().lowercase()

    private data class AttemptState(
        val failedAttempts: Int,
        val lockedUntil: Instant?
    )
}
