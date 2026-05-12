package ch.taskify.service.team

import ch.taskify.dto.TeamDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.team.Team
import ch.taskify.entity.user.Role
import ch.taskify.entity.user.UserEntity
import ch.taskify.repository.TeamRepository
import ch.taskify.repository.UserRepository
import ch.taskify.utils.CurrentUser
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class TeamServiceImpl(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository
) : TeamService {

    private val log = LoggerFactory.getLogger(TeamServiceImpl::class.java)

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

    @Transactional(readOnly = true)
    override fun findByLeaderId(userId: UUID): List<TeamDTO> {

        log.info("Fetching leader teams for userId={}, actor={}", userId, CurrentUser.name)

        val teams = teamRepository.findByTeamLeaderId(userId)
            .sortedBy { it.name.orEmpty().lowercase() }

        log.info(
            "Leader teams loaded: userId={}, count={}, actor={}",
            userId,
            teams.size,
            CurrentUser.name
        )

        return teams.map { it.toDTO() }
    }

    override fun createTeam(name: String, userDTO: UserDTO ,description: String?): TeamDTO {
        requireAdmin()

        val normalizedName = name.trim()
        require(normalizedName.isNotEmpty()) { "Teamname darf nicht leer sein." }
        require(!teamRepository.existsByNameIgnoreCase(normalizedName)) {
            "Team '$normalizedName' existiert bereits."
        }

        val user = userRepository.findById(userDTO.id!!)
            .orElseThrow { IllegalArgumentException("User nicht gefunden") }

        val saved = teamRepository.save(
            Team().apply {
                this.name = normalizedName
                this.teamLeader = user
                this.description = description?.trim()?.takeIf { it.isNotEmpty() }
            }
        )

        log.info("Team created: id={}, name={}, actor={}", saved.id, saved.name, CurrentUser.name)
        return saved.toDTO()
    }

    override fun updateMembers(teamId: UUID, userIds: Set<UUID>): List<UserDTO> {
        val team = teamRepository.findById(teamId)
            .orElseThrow { IllegalArgumentException("Team existiert nicht.") }

        requireAdminOrLeaderOf(team)

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

        log.info(
            "Team members updated: teamId={}, memberCount={}, actor={}",
            teamId,
            selectedUsers.size,
            CurrentUser.name
        )

        return selectedUsers
            .sortedBy { it.name.lowercase() }
            .map { it.toDTO() }
    }

    private fun requireAdminOrLeaderOf(team: Team) {
        val currentUser = CurrentUser.principalAsUserEntity
            ?: throw AccessDeniedException("Kein Benutzer angemeldet.")

        val isAdmin = currentUser.role == Role.ADMIN
        val isLeader = team.teamLeader?.id == currentUser.id

        if (!isAdmin && !isLeader) {
            log.warn(
                "Denied team member update: actor={}, teamId={}, leaderId={}",
                CurrentUser.name,
                team.id,
                team.teamLeader?.id
            )
            throw AccessDeniedException("Nur Admin oder Teamleiter dieses Teams dürfen Mitglieder verwalten.")
        }
    }

    private fun Team.toDTO(): TeamDTO = TeamDTO(
        id = id,
        name = name.orEmpty(),
        description = description,
        teamLeaderId = teamLeader?.id,
        teamLeaderName = teamLeader?.name
    )

    private fun ch.taskify.entity.user.UserEntity.toDTO(): UserDTO = UserDTO(
        id = id,
        name = name,
        passwordHash = passwordHash,
        role = role
    )

    private fun requireAdmin() {
        if (CurrentUser.principalAsUserEntity?.role != Role.ADMIN) {
            log.warn("Denied team administration action: actor={}", CurrentUser.name)
            throw AccessDeniedException("Nur Admins duerfen Teams erstellen.")
        }
    }
}