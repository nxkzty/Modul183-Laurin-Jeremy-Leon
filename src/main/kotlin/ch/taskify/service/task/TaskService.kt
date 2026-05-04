package ch.taskify.service.task

import ch.taskify.entity.task.Task
import java.util.UUID

interface TaskService {

    fun create(task: Task): Task

    fun getById(id: UUID): Task

    fun getAll(): List<Task>

    fun update(id: UUID, task: Task): Task

    fun delete(id: UUID)
}