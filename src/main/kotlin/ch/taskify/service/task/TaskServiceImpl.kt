package ch.taskify.service.task

import ch.taskify.entity.task.Task
import ch.taskify.repository.TaskRepository
import org.springframework.stereotype.Service
import jakarta.persistence.EntityNotFoundException
import java.util.UUID

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository
) : TaskService {

    override fun create(task: Task): Task {
        return taskRepository.save(task)
    }

    override fun getById(id: UUID): Task {
        return taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }
    }

    override fun getAll(): List<Task> {
        return taskRepository.findAll()
    }

    override fun update(id: UUID, task: Task): Task {
        val existing = getById(id)

        existing.title = task.title
        existing.description = task.description
        existing.state = task.state
        existing.assignee = task.assignee
        existing.issuer = task.issuer
        existing.risk = task.risk

        return taskRepository.save(existing)
    }

    override fun delete(id: UUID) {
        if (!taskRepository.existsById(id)) {
            throw EntityNotFoundException("Task with id $id not found")
        }
        taskRepository.deleteById(id)
    }
}