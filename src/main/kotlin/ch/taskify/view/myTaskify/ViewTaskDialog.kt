package ch.taskify.view.myTaskify

import ch.taskify.dto.TaskDTO
import ch.taskify.utils.dialog.TADialog
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class ViewTaskDialog(
    private val task: TaskDTO,
    private val onEdit: () -> Unit,
) : TADialog(
    titleText = task.title,
    subtitleText = "Aufgabe Details"
) {

    init {
        setDialogWidth("640px")
        buildContent()

        val editButton = Button("Bearbeiten", Icon(VaadinIcon.EDIT)) {
            close()
            onEdit()
        }

        setButtons(primary(editButton), closeButton("Schliessen"))
    }

    private fun buildContent() {
        val content = VerticalLayout().apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "16px")
        }

        val badgeRow = HorizontalLayout().apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
            alignItems = FlexComponent.Alignment.CENTER
        }
        badgeRow.add(TaskBadges.stateBadge(task.state))
        task.risk?.let { badgeRow.add(TaskBadges.riskBadge(it)) }

        content.add(badgeRow)

        if (task.description.isNotBlank()) {
            content.add(section("Beschreibung", Span(task.description).apply {
                style
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)")
                    .set("line-height", "1.6")
                    .set("white-space", "pre-wrap")
            }))
        }

        task.assigneeUsername?.let { content.add(section("Zuständig", userRow(it))) }
        task.issuerUsername?.let { content.add(section("Erstellt von", userRow(it))) }

        addContent(content)
    }

    private fun userRow(username: String): HorizontalLayout {
        val avatar = Avatar(username).apply {
            setWidth("28px")
            setHeight("28px")
        }
        val name = Span(username).apply {
            style.set("font-size", "var(--lumo-font-size-s)")
        }
        return HorizontalLayout(avatar, name).apply {
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
        }
    }

    private fun section(label: String, valueComponent: Component): VerticalLayout {
        val labelSpan = Span(label).apply {
            style
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
                .set("color", "var(--lumo-tertiary-text-color)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.05em")
        }
        return VerticalLayout(labelSpan, valueComponent).apply {
            isPadding = false
            isSpacing = false
            style
                .set("gap", "4px")
                .set("padding", "12px")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "8px")
        }
    }
}