package ch.taskify.dto

import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import java.util.UUID

data class TaskDTO(
    val id: UUID? = null,
    var title: String = "",
    var description: String = "",
    var state: State = State.OPEN,
    var risk: Risk? = null,
    var assigneeUsername: String? = null,
    var issuerUsername: String? = null

)