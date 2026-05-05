package ch.taskify.view.myTaskify

import ch.taskify.dto.TaskDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import ch.taskify.service.task.TaskService
import ch.taskify.utils.dialog.TADialog
import ch.taskify.utils.notify.Notify
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder

class CreateTaskDialog(
    private val taskService: TaskService,
    private val users: List<UserDTO>,
    private val currentUsername: String,
    private val onSave: () -> Unit,
) : TADialog(
    titleText = "Aufgabe erstellen",
    subtitleText = "Erstelle eine Aufgabe und weise sie einem Mitarbeiter zu."
) {

    private val binder = Binder(TaskDTO::class.java)
    private val task = TaskDTO()

    private val title = TextField("Titel")
    private val description = TextArea("Beschreibung")
    private val state = ComboBox<State>("Status")
    private val risk = ComboBox<Risk>("Risiko")
    private val assignee = ComboBox<UserDTO>("Zuständig")

    init {

        state.setItems(State.entries)
        risk.setItems(Risk.entries)

        assignee.setItems(users)
        assignee.setItemLabelGenerator { it.name }

//        issuer.setItems(users)
//        issuer.setItemLabelGenerator { it.name }

        title.isRequiredIndicatorVisible = true
        state.isRequiredIndicatorVisible = true
        assignee.isRequiredIndicatorVisible = true
//        issuer.isRequiredIndicatorVisible = true

        binder.forField(title)
            .asRequired("Titel ist erforderlich")
            .withValidator({ it.length >= 3 }, "Mindestens 3 Zeichen")
            .bind(TaskDTO::title, TaskDTO::title::set)

        binder.forField(description)
            .withValidator({ it.length <= 500 }, "Maximal 500 Zeichen")
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

//        binder.forField(issuer)
//            .withConverter(
//                { user: UserDTO? -> user?.name },
//                { username: String? -> users.find { it.name == username } }
//            )
//            .bind(TaskDTO::issuerUsername, TaskDTO::issuerUsername::set)

        binder.readBean(task)

        val form = FormLayout().apply {
            responsiveSteps = listOf(
                FormLayout.ResponsiveStep("0", 1),
                FormLayout.ResponsiveStep("600px", 1)
            )
        }

        form.add(title, description, state, risk, assignee)

        val saveButton = primary(Button("Speichern") { save() })
        val cancelButton = closeButton()

        setButtons(saveButton, cancelButton)
        addContent(form)
    }

    private fun save() {
        task.issuerUsername = currentUsername
        task.assigneeUsername = assignee.value.name
        if (binder.writeBeanIfValid(task)) {
            taskService.create(task)
            Notify.success("Task erstellt!")
            onSave()
            close()
        } else {
            Notify.error("Bitte überprüfe die Eingaben!")
        }
    }
}