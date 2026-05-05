package ch.taskify.utils.notify

import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/*
 * Notify.java  
 *
 * Creator:
 * 05.05.2026 08:22 laurin.ebnoether
 *
 * Maintainer:
 * 05.05.2026 08:22 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */



object Notify {

    fun success(message: String) = show(message, Type.SUCCESS)
    fun error(message: String) = show(message, Type.ERROR)
    fun warning(message: String) = show(message, Type.WARNING)
    fun info(message: String) = show(message, Type.INFO)

    private fun show(message: String, type: Type) {
        val notification = Notification()
        notification.duration = type.duration
        notification.position = Notification.Position.BOTTOM_END

        val icon = Icon(type.icon).apply {
            style.set("color", "white")
        }

        val text = Span(message)

        val layout = HorizontalLayout(icon, text).apply {
            isPadding = false
            isSpacing = true
            alignItems = com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER
        }

        notification.add(layout)

        // Styling je nach Typ
        notification.element.style.apply {
            set("background-color", type.bgColor)
            set("color", "white")
            set("border-radius", "8px")
            set("padding", "10px 14px")
        }

        notification.open()
    }

    private enum class Type(
        val bgColor: String,
        val icon: VaadinIcon,
        val duration: Int
    ) {
        SUCCESS("#10b981", VaadinIcon.CHECK, 1500),
        ERROR("#ef4444", VaadinIcon.CLOSE_SMALL, 3000),
        WARNING("#f59e0b", VaadinIcon.WARNING, 2500),
        INFO("#3b82f6", VaadinIcon.INFO_CIRCLE, 1500)
    }
}