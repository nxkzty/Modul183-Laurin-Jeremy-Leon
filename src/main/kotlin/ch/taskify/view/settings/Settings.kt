package ch.taskify.view.settings

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll

/*
 * Settings.java  
 *
 * Creator:
 * 04.05.2026 15:44 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 15:44 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */
@Route("settings")
@PageTitle("MyTaskify")
@PermitAll
class Settings : VerticalLayout() {

    @PostConstruct
    fun init() {
        add("Hello Settings")
    }

}