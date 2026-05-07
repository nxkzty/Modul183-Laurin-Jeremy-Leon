package ch.taskify.view.myTaskify.board

import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.State
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class BoardPeopleSidebar(
    private val tasks: List<TaskDTO>,
    initiallyExpanded: Boolean = true,
    private val onExpandedChange: ((Boolean) -> Unit)? = null,
) : VerticalLayout() {

    private var isExpanded = true
    private val contentWrapper = VerticalLayout()

    private val toggleIcon = Icon(VaadinIcon.ANGLE_LEFT)
    private lateinit var headerLabel: Span

    init {
        width = "320px"
        minWidth = "320px"
        maxWidth = "320px"

        isPadding = false
        isSpacing = false

        style
            .set("gap", "12px")
            .set("padding", "0")
            .set("box-sizing", "border-box")
            .set("overflow", "hidden")
            .set("transition", "width 0.3s ease, min-width 0.3s ease, max-width 0.3s ease")

        add(buildHeader(), buildContentWrapper())

        setExpanded(initiallyExpanded)
    }

    private fun buildHeader(): HorizontalLayout {
        val icon = Icon(VaadinIcon.USERS).apply {
            style
                .set("width", "16px")
                .set("height", "16px")
                .set("color", "#64748b")
        }

        headerLabel = Span("Offene Aufgaben pro Person").apply {
            style
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.08em")
                .set("color", "#64748b")
        }

        toggleIcon.apply {
            style
                .set("width", "16px")
                .set("height", "16px")
                .set("color", "#94a3b8")
                .set("cursor", "pointer")
                .set("transition", "transform 0.3s ease")
        }

        return HorizontalLayout(icon, headerLabel, toggleIcon).apply {
            setWidthFull()

            isPadding = false
            isSpacing = false

            alignItems = FlexComponent.Alignment.CENTER
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN

            style
                .set("gap", "8px")
                .set("padding", "10px 14px")
                .set("box-sizing", "border-box")
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "14px")
                .set("cursor", "pointer")
                .set("user-select", "none")

            addClickListener {
                toggle()
            }
        }
    }

    private fun buildContentWrapper(): VerticalLayout {

        contentWrapper.apply {

            setWidthFull()

            isPadding = false
            isSpacing = false

            style
                .set("gap", "10px")
                .set("padding", "14px")
                .set("box-sizing", "border-box")
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "14px")
                .set("transition", "opacity 0.3s ease")
        }

        contentWrapper.add(buildContent())

        return contentWrapper
    }

    private fun buildContent(): VerticalLayout {
        val openTasks = tasks.filter { it.state != State.COMPLETE }
        val grouped = openTasks.groupBy { it.assigneeUsername ?: "Nicht zugewiesen" }
                .toList()
                .sortedByDescending { it.second.size }

        val rows = grouped.map { (username, tasks) ->
            personRow(username, tasks.size)
        }

        return VerticalLayout(*rows.toTypedArray()).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            style.set("gap", "10px")
        }
    }

    private fun personRow(
        username: String,
        count: Int
    ): HorizontalLayout {

        val avatar = Avatar(username).apply {
            name = username
            setWidth("30px")
            setHeight("30px")
        }

        val name = Span(username).apply {
            style
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("color", "#334155")
        }

        val countBadge = Div(Span(count.toString())).apply {
            style
                .set("min-width", "28px")
                .set("height", "28px")
                .set("padding", "0 10px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background", "#eff6ff")
                .set("border", "1px solid #bfdbfe")
                .set("border-radius", "999px")
                .set("color", "#2563eb")
                .set("font-size", "12px")
                .set("font-weight", "700")
        }

        val userLayout = HorizontalLayout(avatar, name).apply {
            isPadding = false
            isSpacing = false
            alignItems = FlexComponent.Alignment.CENTER
            style.set("gap", "10px")
        }

        return HorizontalLayout(userLayout, countBadge).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
            style
                .set("padding", "8px 0")
                .set("border-bottom", "1px solid rgba(148, 163, 184, 0.12)")
        }
    }

    private fun toggle() {
        setExpanded(!isExpanded)
        onExpandedChange?.invoke(isExpanded)
    }

    private fun setExpanded(expanded: Boolean) {
        isExpanded = expanded
        if (expanded) {
            width = "320px"
            minWidth = "320px"
            maxWidth = "320px"
            contentWrapper.style
                .set("opacity", "1")
                .set("pointer-events", "auto")

            headerLabel.isVisible = true
            toggleIcon.isVisible = true
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
        } else {
            width = "56px"
            minWidth = "56px"
            maxWidth = "56px"
            contentWrapper.style
                .set("opacity", "0")
                .set("pointer-events", "none")

            headerLabel.isVisible = false
            toggleIcon.isVisible = false
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        }
    }
}