package ch.taskify.dto

import ch.taskify.entity.user.Role

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
data class UserDTO(val name: String,
    val passwordHash: String)
