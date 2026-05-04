package ch.taskify.repository

import ch.taskify.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/*
 * UserRepository.java  
 *
 * Creator:
 * 04.05.2026 08:19 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 08:19 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByNameIgnoreCase(username: String): UserEntity?
    fun existsByNameIgnoreCase(username: String): Boolean
}
