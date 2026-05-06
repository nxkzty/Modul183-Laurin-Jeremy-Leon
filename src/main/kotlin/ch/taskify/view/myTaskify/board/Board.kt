package ch.taskify.view.myTaskify.board

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

/*
 * Board.java  
 *
 * Creator:
 * 06.05.2026 10:51 laurin.ebnoether
 *
 * Maintainer:
 * 06.05.2026 10:51 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

@Route("myTaskify/board")
@PageTitle("Board")
@PermitAll
class Board : VerticalLayout() {

    init {
        add("Board")
    }

}