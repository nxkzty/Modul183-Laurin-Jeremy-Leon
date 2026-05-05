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
import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.Task
import java.util.UUID

interface TaskService {

    fun create(task: TaskDTO): TaskDTO

    fun getById(id: UUID): TaskDTO

    fun getAll(): List<TaskDTO>

    fun update(id: UUID, task: TaskDTO): TaskDTO

    fun delete(id: UUID)

    fun getAllFromCurrentUser(): List<TaskDTO>
}