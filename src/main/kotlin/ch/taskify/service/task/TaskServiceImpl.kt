package ch.taskify.service.task

import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.Task
import ch.taskify.repository.TaskRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class TaskServiceImpl(
    private val taskRepository: TaskRepository
) : TaskService {

    override fun create(task: TaskDTO): TaskDTO {
        val entity = task.toEntity()
        return taskRepository.save(entity).toDto()
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): TaskDTO {
        return taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }
            .toDto()
    }

    @Transactional(readOnly = true)
    override fun getAll(): List<TaskDTO> {
        return taskRepository.findAll().map { it.toDto() }
    }

    override fun update(id: UUID, task: TaskDTO): TaskDTO {
        val existing = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }

        existing.title = task.title
        existing.description = task.description
        existing.state = task.state
        existing.risk = task.risk

        return taskRepository.save(existing).toDto()
    }

    override fun delete(id: UUID) {
        if (!taskRepository.existsById(id)) {
            throw EntityNotFoundException("Task with id $id not found")
        }
        taskRepository.deleteById(id)
    }

    private fun Task.toDto(): TaskDTO =
        TaskDTO(
            id = this.id!!,
            title = this.title,
            description = this.description,
            state = this.state,
            risk = this.risk,
            assigneeUsername = this.assignee?.name,
            issuerUsername = this.issuer?.name
        )

    private fun TaskDTO.toEntity(): Task =
        Task().apply {
            title = this@toEntity.title
            description = this@toEntity.description
            state = this@toEntity.state
            risk = this@toEntity.risk
        }
}