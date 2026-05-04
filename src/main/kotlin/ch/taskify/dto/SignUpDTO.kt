package ch.taskify.dto

import jakarta.validation.constraints.Size

/*
 * SignUpDTO.java  
 *
 * Creator:
 * 04.05.2026 11:17 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 11:17 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */
data class SignUpDTO(@Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters.") val username: String,
                     @Size(min = 8, max = 256, message = "Password must be at least 8 characters long (256 max).") val password: String)
