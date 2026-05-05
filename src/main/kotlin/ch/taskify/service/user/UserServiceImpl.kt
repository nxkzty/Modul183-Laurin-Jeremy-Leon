package ch.taskify.service.user

import ch.taskify.dto.UserDTO
import ch.taskify.entity.user.Role
import ch.taskify.entity.user.UserEntity
import ch.taskify.repository.UserRepository
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

    @Transactional(readOnly = true)
    override fun findAll(): List<UserDTO> = userRepository.findAll().map { userDTO ->
        UserDTO(userDTO.name, userDTO.passwordHash, userDTO.role)
    }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): UserDTO? {
        val findById = userRepository.findById(id)
        return if (findById.isPresent) {
            UserDTO(findById.get().name, findById.get().passwordHash, findById.get().role)
        } else {
            return null
        }
    }

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): UserDTO? {
        val findByNameIgnoreCase = userRepository.findByNameIgnoreCase(username.trim())
        return if (findByNameIgnoreCase != null) {
            UserDTO(findByNameIgnoreCase.name, findByNameIgnoreCase.passwordHash, findByNameIgnoreCase.role)
        }else {
            null
        }
    }

    override fun createUser(
        username: String,
        rawPassword: String,
        role: Role
    ): UserDTO {
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
        return UserDTO(saved.name, saved.passwordHash, saved.role)
    }

    override fun updateUser(
        id: UUID,
        username: String?,
        rawPassword: String?,
        role: Role?
    ): UserDTO {
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
        return UserDTO(saved.name, saved.passwordHash, saved.role)
    }

    override fun deleteUser(id: UUID) {
        require(userRepository.existsById(id)) { "User with id '$id' does not exist" }
        userRepository.deleteById(id)
    }
}
