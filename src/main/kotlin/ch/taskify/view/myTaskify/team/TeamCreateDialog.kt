package ch.taskify.view.myTaskify.team

import ch.taskify.utils.dialog.TADialog
import ch.taskify.utils.notify.Notify
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField

class TeamCreateDialog(
    private val onCreate: (name: String, description: String?) -> Unit
) : TADialog(
    titleText = "Team erstellen",
    subtitleText = "Erstelle ein neues Team und füge danach Mitglieder hinzu."
) {

    private val nameField = TextField("Teamname")
    private val descriptionField = TextArea("Beschreibung")

    init {
        setDialogWidth("560px")

        nameField.isRequiredIndicatorVisible = true
        nameField.width = "100%"

        descriptionField.width = "100%"
        descriptionField.maxLength = 500

        addContent(
            FormLayout(nameField, descriptionField).apply {
                responsiveSteps = listOf(FormLayout.ResponsiveStep("0", 1))
            }
        )

        setButtons(
            primary(Button("Team erstellen") { save() }),
            closeButton()
        )
    }

    private fun save() {
        val name = nameField.value.trim()
        if (name.isBlank()) {
            Notify.error("Teamname ist erforderlich.")
            return
        }

        val description = descriptionField.value.trim().takeIf { it.isNotBlank() }
        onCreate(name, description)
        close()
    }
}