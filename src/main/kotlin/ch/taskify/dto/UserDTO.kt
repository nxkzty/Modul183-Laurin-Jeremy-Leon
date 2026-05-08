package ch.taskify.dto

import ch.taskify.entity.user.Role
import java.util.UUID

/*
 * UserDTO.java  
 *
 * Creator:
 * 04.05.2026 14:44 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 14:44 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */
data class UserDTO(
    val id : UUID?,
    var name: String,
    var passwordHash: String,
    var role: Role
)


