package ch.taskify.view.myTaskify.team

import ch.taskify.dto.TeamDTO
import ch.taskify.dto.UserDTO
import ch.taskify.utils.dialog.TADialog
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.data.renderer.ComponentRenderer
import java.util.UUID

class TeamMembersDialog(
    private val team: TeamDTO,
    users: List<UserDTO>,
    selectedMembers: List<UserDTO>,
    private val onSave: (userIds: Set<UUID>) -> Unit
) : TADialog(
    titleText = "Mitglieder hinzufügen",
    subtitleText = "Mitglieder für ${team.name} auswählen."
) {

    private val members = MultiSelectComboBox<UserDTO>("Mitglieder")

    init {
        setDialogWidth("620px")

        members.width = "100%"
        members.placeholder = "User auswählen"
        members.setItems(users)
        members.setItemLabelGenerator { it.name }
        members.setRenderer(userRenderer())
        members.setValue(
            users.filter { user -> selectedMembers.any { it.id == user.id } }.toSet()
        )

        addContent(members)

        setButtons(
            primary(Button("Mitglieder speichern") { save() }),
            closeButton()
        )
    }

    private fun save() {
        onSave(members.selectedItems.mapNotNull { it.id }.toSet())
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