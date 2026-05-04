package ch.taskify.view.myTaskify

import ch.taskify.dto.TaskDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import ch.taskify.entity.task.Task
import ch.taskify.entity.user.UserEntity
import ch.taskify.service.task.TaskService
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder

class CreateTaskDialog(
    private val taskService: TaskService,
    private val users: List<UserDTO>,
    private val onSave: () -> Unit,
) : Dialog("Aufgabe erstellen") {

    private val binder = Binder(TaskDTO::class.java)

    private val task = TaskDTO()

    private val title = TextField("Titel")
    private val description = TextArea("Beschreibung")

    private val state = ComboBox<State>("Status")
    private val risk = ComboBox<Risk>("Risiko")

    private val assignee = ComboBox<UserDTO>("Zuständig")
    private val issuer = ComboBox<UserDTO>("Erstellt von")

    init {
        width = "500px"

        state.setItems(State.entries)
        risk.setItems(Risk.entries)

        assignee.setItems(users)
        assignee.setItemLabelGenerator { it.name }

        issuer.setItems(users)
        issuer.setItemLabelGenerator { it.name }

        binder.bindInstanceFields(this)
        binder.readBean(task)

        val form = FormLayout(title, description, state, risk, assignee, issuer)

        val saveButton = Button("Speichern") { save() }
        val cancelButton = Button("Abbrechen") { close() }

        add(form, HorizontalLayout(saveButton, cancelButton))
    }

    private fun save() {
        if (binder.writeBeanIfValid(task)) {
            taskService.create(task)
            Notification.show("Task erstellt!")
            onSave()
            close()
        } else {
            Notification.show("Bitte überprüfe die Eingaben!")
        }
    }
}