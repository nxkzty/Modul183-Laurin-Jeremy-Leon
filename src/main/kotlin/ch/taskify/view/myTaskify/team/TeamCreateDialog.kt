package ch.taskify.view.myTaskify.team

import ch.taskify.dto.UserDTO
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
import com.vaadin.flow.data.renderer.ComponentRenderer

class TeamCreateDialog(
    private val onCreate: (name: String, teamLeader: UserDTO, description: String?) -> Unit,
    users: List<UserDTO>
) : TADialog(
    titleText = "Team erstellen",
    subtitleText = "Erstelle ein neues Team und füge danach Mitglieder hinzu."
) {

    private val nameField = TextField("Teamname")
    private val leader = ComboBox<UserDTO>("Teamleiter")
    private val descriptionField = TextArea("Beschreibung")

    init {
        setDialogWidth("560px")

        nameField.isRequiredIndicatorVisible = true
        nameField.width = "100%"

        descriptionField.width = "100%"
        descriptionField.maxLength = 500

        leader.width = "100%"
        leader.placeholder = "Teamleiter auswählen"
        leader.setItems(users)
        leader.setItemLabelGenerator { it.name }
        leader.setRenderer(userRenderer())

        addContent(leader)

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
        onCreate(name, leader.value , description)
        close()
    }

    private fun userRenderer(): ComponentRenderer<HorizontalLayout, UserDTO> {
        return ComponentRenderer { user ->
            HorizontalLayout(
                Avatar(user.name),
                Span(user.name)
            ).apply {
                defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
                isPadding = false
                isSpacing = true
            }
        }
    }
}