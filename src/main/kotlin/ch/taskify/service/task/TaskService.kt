package ch.taskify.service.task

import ch.taskify.entity.task.Task

interface TaskService {

    fun create(task: Task): Task

    fun getById(id: Long): Task

    fun getAll(): List<Task>

    fun update(id: Long, task: Task): Task

    fun delete(id: Long)
}