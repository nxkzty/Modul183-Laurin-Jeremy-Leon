package ch.taskify.view.myTaskify.team

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

/*
 * Team.java  
 *
 * Creator:
 * 06.05.2026 10:53 laurin.ebnoether
 *
 * Maintainer:
 * 06.05.2026 10:53 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

@Route("myTaskify/team")
@PageTitle("Mein Team")
@PermitAll
class Team : VerticalLayout() {

    init {
        add("Mein Team")
    }

}