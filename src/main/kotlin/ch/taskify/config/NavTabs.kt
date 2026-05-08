package ch.taskify.config

import com.vaadin.flow.component.Component

/*
 * NavTabs.java
 *
 * Creator:
 * 05.05.2026 14:27 laurin.ebnoether
 *
 * Maintainer:
 * 05.05.2026 14:27 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */
data class NavTabs(
    val label: String,
    val route: String,
    val view: Class<out Component>
)