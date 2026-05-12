package ch.taskify.service.team

import ch.taskify.dto.TeamDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.team.Team
import ch.taskify.repository.TeamRepository
import ch.taskify.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class TeamServiceImpl(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository
) : TeamService {

    @Transactional(readOnly = true)
    override fun findAll(): List<TeamDTO> {
        return teamRepository.findAll()
            .sortedBy { it.name.orEmpty().lowercase() }
            .map { it.toDTO() }
    }

    @Transactional(readOnly = true)
    override fun findByUserId(userId: UUID): List<TeamDTO> {
        return teamRepository.findByUserId(userId)
            .sortedBy { it.name.orEmpty().lowercase() }
            .map { it.toDTO() }
    }

    @Transactional(readOnly = true)
    override fun findMembers(teamId: UUID): List<UserDTO> {
        return userRepository.findByTeamsId(teamId)
            .sortedBy { it.name.lowercase() }
            .map { it.toDTO() }
    }

    override fun createTeam(
        name: String,
        description: String?
    ): TeamDTO {
        val normalizedName = name.trim()
        require(normalizedName.isNotEmpty()) { "Teamname darf nicht leer sein." }
        require(!teamRepository.existsByNameIgnoreCase(normalizedName)) {
            "Team '$normalizedName' existiert bereits."
        }

        val saved = teamRepository.save(
            Team().apply {
                this.name = normalizedName
                this.description = description?.trim()?.takeIf { it.isNotEmpty() }
            }
        )

        return saved.toDTO()
    }

    override fun updateMembers(
        teamId: UUID,
        userIds: Set<UUID>
    ): List<UserDTO> {
        val team = teamRepository.findById(teamId)
            .orElseThrow { IllegalArgumentException("Team existiert nicht.") }

        val selectedUsers = userRepository.findAllById(userIds).toList()
        require(selectedUsers.size == userIds.size) { "Mindestens ein User existiert nicht." }

        val previousMembers = userRepository.findByTeamsId(teamId)
        previousMembers.forEach { user ->
            user.teams.removeIf { it.id == teamId }
        }

        selectedUsers.forEach { user ->
            user.teams.add(team)
        }

        userRepository.saveAll((previousMembers + selectedUsers).distinctBy { it.id })

        return selectedUsers
            .sortedBy { it.name.lowercase() }
            .map { it.toDTO() }
    }

    private fun Team.toDTO(): TeamDTO = TeamDTO(
        id = id,
        name = name.orEmpty(),
        description = description
    )

    private fun ch.taskify.entity.user.UserEntity.toDTO(): UserDTO = UserDTO(
        id = id,
        name = name,
        passwordHash = passwordHash,
        role = role
    )
}
