package ch.taskify.service.user

import ch.taskify.dto.UserDTO
import ch.taskify.entity.user.Role
import ch.taskify.entity.user.UserEntity
import ch.taskify.repository.UserRepository
import ch.taskify.utils.CurrentUser
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun findAll(): List<UserDTO> = userRepository.findAll().map { userDTO ->
        UserDTO(userDTO.id, userDTO.name, userDTO.passwordHash, userDTO.role)
    }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): UserDTO? {
        val findById = userRepository.findById(id)
        return if (findById.isPresent) {
            UserDTO(id, findById.get().name, findById.get().passwordHash, findById.get().role)
        } else {
            null
        }
    }

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): UserDTO? {
        val findByNameIgnoreCase = userRepository.findByNameIgnoreCase(username.trim())
        return if (findByNameIgnoreCase != null) {
            UserDTO(
                findByNameIgnoreCase.id,
                findByNameIgnoreCase.name,
                findByNameIgnoreCase.passwordHash,
                findByNameIgnoreCase.role
            )
        }else {
            null
        }
    }

    override fun createUser(
        username: String,
        rawPassword: String,
        role: Role
    ): UserDTO {
        if (role != Role.USER) {
            requireAdmin()
        }

        val normalizedUsername = username.trim()
        require(normalizedUsername.isNotEmpty()) { "Username must not be blank" }
        require(rawPassword.isNotBlank()) { "Password must not be blank" }
        require(!userRepository.existsByNameIgnoreCase(normalizedUsername)) {
            "User with username '$normalizedUsername' already exists"
        }

        val saved =  userRepository.save(
            UserEntity().apply {
                name = normalizedUsername
                passwordHash = passwordEncoder.encode(rawPassword)
                    ?: throw IllegalStateException("Password encoder returned null")
                this.role = role
            }
        )

        log.info("User created: id={}, username={}, role={}, actor={}", saved.id, saved.name, saved.role, CurrentUser.name)
        return UserDTO(saved.id, saved.name, saved.passwordHash, saved.role)
    }

    override fun updateUser(
        id: UUID,
        username: String?,
        rawPassword: String?,
        role: Role?
    ): UserDTO {
        requireSelfOrAdmin(id, role)

        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User with id '$id' does not exist") }

        username?.trim()?.let { normalizedUsername ->
            require(normalizedUsername.isNotEmpty()) { "Username must not be blank" }
            val existingUser = userRepository.findByNameIgnoreCase(normalizedUsername)
            require(existingUser == null || existingUser.id == user.id) {
                "User with username '$normalizedUsername' already exists"
            }
            user.name = normalizedUsername
        }

        rawPassword?.takeIf { it.isNotBlank() }?.let {
            user.passwordHash = passwordEncoder.encode(it)
                ?: throw IllegalStateException("Password encoder returned null")
        }

        role?.let {
            user.role = it
        }

        val saved = userRepository.save(user)
        log.info("User updated: id={}, username={}, role={}, actor={}", saved.id, saved.name, saved.role, CurrentUser.name)
        return UserDTO(saved.id, saved.name, saved.passwordHash, saved.role)
    }

    override fun deleteUser(id: UUID) {
        requireAdmin()
        require(userRepository.existsById(id)) { "User with id '$id' does not exist" }
        userRepository.deleteById(id)
        log.info("User deleted: id={}, actor={}", id, CurrentUser.name)
    }

    private fun requireSelfOrAdmin(targetUserId: UUID, requestedRole: Role?) {
        val actor = CurrentUser.principalAsUserEntity
            ?: throw AccessDeniedException("Nicht angemeldet.")

        val isAdmin = actor.role == Role.ADMIN
        val isSelf = actor.id == targetUserId

        if (!isAdmin && (!isSelf || requestedRole != null)) {
            log.warn("Denied user update: targetUserId={}, actor={}", targetUserId, CurrentUser.name)
            throw AccessDeniedException("Kein Zugriff auf diesen User.")
        }
    }

    private fun requireAdmin() {
        if (CurrentUser.principalAsUserEntity?.role != Role.ADMIN) {
            log.warn("Denied user administration action: actor={}", CurrentUser.name)
            throw AccessDeniedException("Nur Admins duerfen User verwalten.")
        }
    }
}
