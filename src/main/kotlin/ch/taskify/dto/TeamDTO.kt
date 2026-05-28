package ch.taskify.dto

import java.util.UUID

data class TeamDTO(
    val id: UUID?,
    var name: String,
    var description: String?,
    val teamLeaderId: UUID?,
    val teamLeaderName: String?
)