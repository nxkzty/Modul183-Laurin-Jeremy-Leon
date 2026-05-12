package ch.taskify.service.team

import ch.taskify.dto.TeamDTO
import ch.taskify.dto.UserDTO
import java.util.UUID

interface TeamService {
    fun findAll(): List<TeamDTO>

    fun findByUserId(userId: UUID): List<TeamDTO>

    fun findMembers(teamId: UUID): List<UserDTO>

    fun createTeam(
        name: String,
        description: String? = null
    ): TeamDTO

    fun updateMembers(
        teamId: UUID,
        userIds: Set<UUID>
    ): List<UserDTO>
}
