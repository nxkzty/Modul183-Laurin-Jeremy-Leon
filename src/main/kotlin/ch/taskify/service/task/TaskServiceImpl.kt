package ch.taskify.service.task

import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.Task
import ch.taskify.entity.user.Role
import ch.taskify.repository.TaskRepository
import ch.taskify.repository.TeamRepository
import ch.taskify.repository.UserRepository
import ch.taskify.utils.CurrentUser
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.util.*

@Service
@Transactional
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val teamRepository: TeamRepository,

) : TaskService {

    private val log = LoggerFactory.getLogger(TaskServiceImpl::class.java)

    override fun create(task: TaskDTO): TaskDTO {
        val entity = task.toEntity()
        requireAssigneeAccess(entity.assignee)

        val saved = taskRepository.save(entity)
        log.info("Task created: id={}, assignee={}, issuer={}", saved.id, saved.assignee?.name, saved.issuer?.name)
        return saved.toDto()
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): TaskDTO {
        val task = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }

        requireTaskAccess(task)
        return task.toDto()
    }

    @Transactional(readOnly = true)
    override fun getAll(): List<TaskDTO> {
        return taskRepository.findAll()
            .filter { canAccessTask(it) }
            .map { it.toDto() }
    }

    override fun update(id: UUID, task: TaskDTO): TaskDTO {
        val existing = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }

        requireTaskAccess(existing)

        existing.title = task.title
        existing.description = task.description
        existing.state = task.state
        existing.risk = task.risk
        existing.assignee = task.assigneeUsername
            ?.trim()
            ?.let { userRepository.findByNameIgnoreCase(it) }
        requireAssigneeAccess(existing.assignee)

        val saved = taskRepository.save(existing)
        log.info("Task updated: id={}, actor={}", saved.id, CurrentUser.name)
        return saved.toDto()
    }

    override fun delete(id: UUID) {
        val task = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }

        requireTaskAccess(task)
        taskRepository.delete(task)
        log.info("Task deleted: id={}, actor={}", id, CurrentUser.name)
    }

    override fun getAllFromCurrentUser(): List<TaskDTO> {
        val principal = CurrentUser.principalAsUserEntity
        val currentUserId = principal?.id ?: return emptyList()
        return taskRepository.findByAssigneeId(currentUserId)
            .map { task -> task.toDto() }
    }

    private fun Task.toDto(): TaskDTO =
        TaskDTO(
            id = this.id!!,
            title = this.title,
            description = this.description,
            state = this.state,
            risk = this.risk,
            assigneeUsername = this.assignee?.name,
            issuerUsername = this.issuer?.name,
            createdAt = this.createdAt
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        )

    private fun TaskDTO.toEntity(): Task =
        Task().apply {
            title = this@toEntity.title
            description = this@toEntity.description
            state = this@toEntity.state
            risk = this@toEntity.risk
            assignee = this@toEntity.assigneeUsername
                ?.trim()
                ?.let { userRepository.findByNameIgnoreCase(it) }

            issuer = this@toEntity.issuerUsername
                ?.trim()
                ?.let { userRepository.findByNameIgnoreCase(it) }
        }

    private fun requireTaskAccess(task: Task) {
        if (!canAccessTask(task)) {
            log.warn("Denied task access: taskId={}, actor={}", task.id, CurrentUser.name)
            throw AccessDeniedException("Kein Zugriff auf diese Aufgabe.")
        }
    }

    private fun requireAssigneeAccess(assignee: ch.taskify.entity.user.UserEntity?) {
        val currentUser = CurrentUser.principalAsUserEntity ?: return
        if (currentUser.role == Role.ADMIN || assignee == null) {
            return
        }

        val currentUserId = currentUser.id ?: return
        if (assignee.id == currentUserId) {
            return
        }

        val teamMemberIds = teamRepository.findByUserId(currentUserId)
            .mapNotNull { it.id }
            .flatMap { teamId -> userRepository.findByTeamsId(teamId) }
            .mapNotNull { it.id }
            .toSet()

        if (assignee.id !in teamMemberIds) {
            log.warn("Denied task assignment: assigneeId={}, actor={}", assignee.id, CurrentUser.name)
            throw AccessDeniedException("Aufgabe darf nur Teammitgliedern zugewiesen werden.")
        }
    }

    private fun canAccessTask(task: Task): Boolean {
        val currentUser = CurrentUser.principalAsUserEntity ?: return false
        val currentUserId = currentUser.id ?: return false

        if (currentUser.role == Role.ADMIN) {
            return true
        }

        val directAccess = task.assignee?.id == currentUserId || task.issuer?.id == currentUserId
        if (directAccess) {
            return true
        }

        val teamMemberIds = teamRepository.findByUserId(currentUserId)
            .mapNotNull { it.id }
            .flatMap { teamId -> userRepository.findByTeamsId(teamId) }
            .mapNotNull { it.id }
            .toSet()

        return task.assignee?.id in teamMemberIds || task.issuer?.id in teamMemberIds
    }
}
