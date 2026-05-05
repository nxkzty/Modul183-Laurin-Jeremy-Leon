package ch.taskify.view.myTaskify

import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import ch.taskify.service.task.TaskService
import ch.taskify.utils.notify.Notify
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/*
 * TasksGrid.java  
 *
 * Creator:
 * 05.05.2026 07:53 laurin.ebnoether
 *
 * Maintainer:
 * 05.05.2026 07:53 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

class TasksGrid(
    private val taskService: TaskService,
    private val onRefresh: () -> Unit,
    private val onEdit: (TaskDTO) -> Unit
) : Grid<TaskDTO>(TaskDTO::class.java, false) {

    init {
        width = "100%"
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES)

        addColumn(TaskDTO::title)
            .setHeader("Titel")
            .setAutoWidth(true)
            .setFlexGrow(1)
            .setSortable(true)

        addColumn { task ->
            if (task.description.length > 50)
                task.description.take(50) + "..."
            else
                task.description
        }.setHeader("Beschreibung")
            .setFlexGrow(2)
            .setSortable(true)

        addComponentColumn { task ->
            Span(task.state.name).apply {
                style.set("background-color", getStateColor(task.state))
                style.set("color", "white")
                style.set("padding", "4px 10px")
                style.set("border-radius", "999px")
                style.set("font-size", "12px")
            }
        }.setHeader("Status")
            .setSortable(true)

        addComponentColumn { task ->
            val text = task.risk?.name ?: "-"
            Span(text).apply {
                style.set("color", getRiskColor(task.risk))
                style.set("font-weight", "600")
            }
        }.setHeader("Risiko")
            .setSortable(true)


        addColumn { it.assigneeUsername ?: "-" }
            .setHeader("Verantwortlich")
            .setSortable(true)

        addColumn { it.issuerUsername ?: "-" }
            .setHeader("Erstellt von")
            .setSortable(true)


        addComponentColumn { task ->
            val edit = Button(Icon(VaadinIcon.EDIT)).apply {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                addClickListener {
                    onEdit(task)
                }
            }


            val delete = Button(Icon(VaadinIcon.TRASH)).apply {
                addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY)
                addClickListener {
                    ConfirmDialog(
                        "Löschen?",
                        "Willst du diese Aufgabe wirklich löschen?",
                        "Löschen",
                        {
                            taskService.delete(task.id!!)
                            onRefresh()
                            Notify.success("Aufgabe wurde gelöscht.")
                        },
                        "Abbrechen",
                        {}
                    ).open()
                }
            }

            HorizontalLayout(edit, delete).apply {
                isPadding = false
                isSpacing = true
            }
        }.setHeader("")
    }

    fun refreshGrid() {
        setItems(taskService.getAll())
    }

    private fun userCell(username: String?): HorizontalLayout {
        val name = username ?: "-"

        val avatar = Avatar(name).apply {
            // Initialen automatisch
            this.name = name
        }

        val label = Span(name)

        return HorizontalLayout(avatar, label).apply {
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = true
        }
    }

    private fun getStateColor(state: State): String {
        return when (state) {
            State.OPEN -> "#6b7280"
            State.TODO -> "#3b82f6"
            State.IN_PROGRESS -> "#f59e0b"
            State.IN_REVIEW -> "#8b5cf6"
            State.COMPLETE -> "#10b981"
        }
    }

    private fun getRiskColor(risk: Risk?): String {
        return when (risk) {
            Risk.LOW -> "#10b981"
            Risk.MEDIUM -> "#f59e0b"
            Risk.HIGH -> "#ef4444"
            null -> "#9ca3af"
        }
    }

}