package ch.taskify.service.task

/*
 * TaskService.java  
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
import java.util.UUID

interface TaskService {

    fun create(task: Task): Task

    fun getById(id: UUID): Task

    fun getAll(): List<Task>

    fun update(id: UUID, task: Task): Task

    fun delete(id: UUID)
}