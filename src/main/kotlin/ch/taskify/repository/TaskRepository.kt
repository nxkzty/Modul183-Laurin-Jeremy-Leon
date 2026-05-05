package ch.taskify.repository

/*
 * TaskRepository.java  
 *
 * Creator:
 * 04.05.2026 16:46 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 16:46 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

import ch.taskify.entity.task.Task
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskRepository : JpaRepository<Task, UUID>