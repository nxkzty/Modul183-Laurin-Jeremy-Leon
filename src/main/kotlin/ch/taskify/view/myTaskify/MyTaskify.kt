package ch.taskify.view.myTaskify

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

/*
 * MyTaskify.java  
 *
 * Creator:
 * 04.05.2026 12:47 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 12:47 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

@Route("myTaskify")
@PermitAll
class MyTaskify : VerticalLayout() {

    fun init() {
        add("Hello MyTaskify")
    }

}