package ch.taskify.view.myTaskify

import ch.taskify.dto.TaskDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import ch.taskify.service.task.TaskService
import ch.taskify.utils.dialog.TADialog
import ch.taskify.utils.notify.Notify
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.renderer.ComponentRenderer

class TaskDialog(
    private val taskService: TaskService,
    private val users: List<UserDTO>,
    private val currentUsername: String,
    private val onSave: () -> Unit,
    existingTask: TaskDTO? = null,
) : TADialog(
    titleText = if (existingTask != null) "Aufgabe bearbeiten" else "Aufgabe erstellen",
    subtitleText = if (existingTask != null) "Änderungen werden sofort gespeichert." else "Erstelle eine Aufgabe und weise sie einem Mitarbeiter zu."
) {

    private val isEditMode = existingTask != null
    private val binder = Binder(TaskDTO::class.java)
    private val task = existingTask ?: TaskDTO()

    private val title = TextField("Titel")
    private val description = TextArea("Beschreibung")
    private val state = ComboBox<State>("Status")
    private val risk = ComboBox<Risk>("Risiko")
    private val assignee = ComboBox<UserDTO>("Verantwortlicher")

    init {
        setDialogWidth("640px")
        buildForm()
    }

    private fun buildForm() {
        state.setItems(State.entries)
        risk.setItems(Risk.entries)

        assignee.setItems(users)
        assignee.setItemLabelGenerator { it.name }
        assignee.setRenderer(assigneeRenderer())

        title.isRequiredIndicatorVisible = true
        state.isRequiredIndicatorVisible = true

        binder.forField(title)
            .asRequired("Titel ist erforderlich")
            .withValidator({ it.length >= 3 }, "Mindestens 3 Zeichen")
            .bind(TaskDTO::title, TaskDTO::title::set)

        binder.forField(description)
            .withValidator({ it.length <= 800 }, "Maximal 800 Zeichen")
            .bind(TaskDTO::description, TaskDTO::description::set)

        binder.forField(state)
            .asRequired("Status ist erforderlich")
            .bind(TaskDTO::state, TaskDTO::state::set)

        binder.forField(risk)
            .bind(TaskDTO::risk, TaskDTO::risk::set)

        binder.forField(assignee)
            .withConverter(
                { user: UserDTO? -> user?.name },
                { username: String? -> users.find { it.name == username } }
            )
            .bind(TaskDTO::assigneeUsername, TaskDTO::assigneeUsername::set)

        binder.readBean(task)

        if (isEditMode) {
            assignee.value = users.find { it.name == task.assigneeUsername }
        }

        val form = FormLayout().apply {
            responsiveSteps = listOf(
                FormLayout.ResponsiveStep("0", 1),
                FormLayout.ResponsiveStep("600px", 1)
            )
            add(title, description, state, risk, assignee)
        }

        val saveButton = primary(Button("Speichern") { save() })
        val cancelButton = closeButton()

        setButtons(saveButton, cancelButton)
        addContent(form)
    }

    private fun assigneeRenderer(): ComponentRenderer<HorizontalLayout?, UserDTO?> =
        ComponentRenderer { user: UserDTO ->
            val avatar = Avatar(user.name).apply {
                setWidth("24px")
                setHeight("24px")
            }
            val name = Span(user.name).apply {
                style.set("font-size", "16px")
            }
            HorizontalLayout(avatar, name).apply {
                alignItems = FlexComponent.Alignment.CENTER
                style.set("gap", "8px")
                isSpacing = true
                isPadding = false
                isMargin = false
            }
        }

    private fun save() {
        if (!isEditMode) task.issuerUsername = currentUsername
        if (binder.writeBeanIfValid(task)) {
            if (isEditMode) {
                taskService.update(task.id!!, task)
                Notify.success("Aufgabe gespeichert!")
            } else {
                taskService.create(task)
                Notify.success("Aufgabe erstellt!")
            }
            onSave()
            close()
        } else {
            Notify.error("Bitte überprüfe die Eingaben!")
        }
    }
}