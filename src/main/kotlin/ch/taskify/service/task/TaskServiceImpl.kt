package ch.taskify.service.task

/*
 * TaskServiceImpl.java  
 *
 * Creator:
 * 04.05.2026 16:40 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 16:40 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */


import ch.taskify.entity.task.Task
import ch.taskify.repository.TaskRepository
import org.springframework.stereotype.Service
import jakarta.persistence.EntityNotFoundException

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository
) : TaskService {

    override fun create(task: Task): Task {
        return taskRepository.save(task)
    }

    override fun getById(id: Long): Task {
        return taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }
    }

    override fun getAll(): List<Task> {
        return taskRepository.findAll()
    }

    override fun update(id: Long, task: Task): Task {
        val existing = getById(id)

        existing.title = task.title
        existing.description = task.description
        existing.state = task.state
        existing.assignee = task.assignee
        existing.issuer = task.issuer
        existing.risk = task.risk

        return taskRepository.save(existing)
    }

    override fun delete(id: Long) {
        if (!taskRepository.existsById(id)) {
            throw EntityNotFoundException("Task with id $id not found")
        }
        taskRepository.deleteById(id)
    }
}