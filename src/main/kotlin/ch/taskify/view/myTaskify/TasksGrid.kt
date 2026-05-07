package ch.taskify.view.myTaskify

import ch.taskify.dto.TaskDTO
import ch.taskify.dto.UserDTO
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

class TasksGrid(
    private val taskService: TaskService,
    private val users: List<UserDTO>,
    private val currentUsername: String,
    private val onRefresh: () -> Unit,
) : Grid<TaskDTO>(TaskDTO::class.java, false) {

    init {
        width = "100%"
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES)
        style
            .set("border-radius", "12px")
            .set("overflow", "hidden")
            .set("box-shadow", "0 1px 4px rgba(0,0,0,0.06)")

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
            TaskBadges.stateBadge(task.state)
        }.setHeader("Status")
            .setSortable(true)

        addComponentColumn { task ->
            TaskBadges.riskBadge(task.risk)
        }.setHeader("Risiko")
            .setSortable(true)

        addComponentColumn { task ->
            userCell(task.assigneeUsername)
        }.setHeader("Verantwortlich")
            .setSortable(true)

        addComponentColumn { task ->
            userCell(task.issuerUsername)
        }.setHeader("Erstellt von")
            .setSortable(true)

        addComponentColumn { task ->
            val edit = Button(Icon(VaadinIcon.EDIT)).apply {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                addClickListener {
                    //todo Laurin: asignee wird noch nicht richtig editiert
                    Notify.warning("Verantwortlicher wird nicht richtig editiert!")
                    CreateTaskDialog(
                        taskService = taskService,
                        users = users,
                        currentUsername = currentUsername,
                        onSave = onRefresh,
                        existingTask = task
                    ).open()
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

        addItemDoubleClickListener { event ->
            ViewTaskDialog(event.item).open()
        }
    }

    private fun userCell(username: String?): HorizontalLayout {
        val name = username ?: "-"
        val avatar = Avatar(name).apply { this.name = name }
        val label = Span(name)
        return HorizontalLayout(avatar, label).apply {
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = true
        }
    }
}