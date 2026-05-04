package ch.taskify.repository

import ch.taskify.entity.task.Task
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<Task, Long>