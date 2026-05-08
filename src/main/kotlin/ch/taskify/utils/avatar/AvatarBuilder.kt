package ch.taskify.utils.avatar

import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.spring.security.AuthenticationContext
import org.springframework.stereotype.Component

/*
 * AvatarBuilder.java  
 *
 * Creator:
 * 05.05.2026 09:37 laurin.ebnoether
 *
 * Maintainer:
 * 05.05.2026 09:37 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

@Component
class AvatarBuilder(
    private val authenticationContext: AuthenticationContext
) {

    companion object {
        private const val ICON_SIZE = "14px"
        private const val FONT_SIZE = "13px"
    }


    fun createAvatar(): Avatar {
        val username = authenticationContext.principalName.orElse("User")

        return Avatar(username).apply {
            style.set("cursor", "pointer")
        }
    }

    fun createAvatarWithMenu(): Avatar {
        val username = authenticationContext.principalName.orElse("User")

        val avatar = Avatar(username).apply {
            style.set("cursor", "pointer")
        }

        createAvatarMenu(avatar)

        return avatar
    }

    public fun createAvatarMenu(avatar: Avatar): ContextMenu {
        return ContextMenu(avatar).apply {
            isOpenOnClick = true

            addItem(createMenuItem(VaadinIcon.COG, "Settings")) {
                ui.ifPresent { it.navigate("settings") }

            }

            addItem(createMenuItem(VaadinIcon.SIGN_OUT, "Logout")) {
                authenticationContext.logout()
                ui.ifPresent { it.navigate("/") }
            }
        }
    }

    private fun createMenuItem(iconType: VaadinIcon, text: String): HorizontalLayout {
        val icon = Icon(iconType).apply {
            setSize(ICON_SIZE)
        }

        val label = Span(text).apply {
            style.set("font-size", FONT_SIZE)
        }

        return HorizontalLayout(icon, label).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
        }
    }

}