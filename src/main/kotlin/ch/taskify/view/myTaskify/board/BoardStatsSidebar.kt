package ch.taskify.view.myTaskify.board

import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class BoardStatsSidebar(private val tasks: List<TaskDTO>) : VerticalLayout() {

    private var isExpanded = true
    private val contentWrapper = VerticalLayout()

    private val titleIcon = Icon(VaadinIcon.BAR_CHART)
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
            .set("padding", "0 0 32px 0")
            .set("align-self", "stretch")
            .set("position", "sticky")
            .set("top", "0")
            .set("flex", "0 0 auto")
            .set("overflow", "hidden")
            .set("transition", "width 0.3s ease, min-width 0.3s ease, max-width 0.3s ease")

        add(buildToggleHeader(), buildScrollableContent())
        setExpanded(true)
    }

    private fun buildToggleHeader(): HorizontalLayout {

        headerLabel = Span("Statistiken").apply {
            style
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.08em")
                .set("color", "#64748b")
        }

        titleIcon.apply {
            style
                .set("width", "16px")
                .set("height", "16px")
                .set("color", "#64748b")
        }

        toggleIcon.apply {
            style
                .set("width", "16px")
                .set("height", "16px")
                .set("color", "#94a3b8")
                .set("transition", "transform 0.3s ease")
                .set("transform", "rotate(0deg)")
                .set("cursor", "pointer")
        }

        return HorizontalLayout(titleIcon, headerLabel, toggleIcon).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
            style
                .set("padding", "10px 14px")
                .set("box-sizing", "border-box")
                .set("background", "rgba(255,255,255,0.82)")
                .set("border", "1px solid rgba(148,163,184,0.24)")
                .set("border-radius", "14px")
                .set("box-shadow", "0 4px 16px rgba(15,23,42,0.06)")
                .set("backdrop-filter", "blur(14px)")
                .set("cursor", "pointer")
                .set("user-select", "none")
                .set("flex-shrink", "0")

            addClickListener { toggleSidebar() }
        }
    }

    private fun buildScrollableContent(): VerticalLayout {
        contentWrapper.apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            style
                .set("gap", "12px")
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
                .set("max-height", "calc(100vh - 120px)")
                .set("padding-right", "4px")
                .set("transition", "max-height 0.35s ease, opacity 0.3s ease")
                .set("scrollbar-width", "thin")

            add(
                sectionCard("Übersicht", overviewContent(tasks)),
                sectionCard("Nach Status", statusContent(tasks)),
                sectionCard("Risiko", riskContent(tasks)),
            )
        }
        return contentWrapper
    }

    private fun toggleSidebar() {
        isExpanded = !isExpanded
        setExpanded(isExpanded)
    }

    private fun setExpanded(expanded: Boolean) {
        if (expanded) {
            width = "320px"
            minWidth = "320px"
            maxWidth = "320px"

            contentWrapper.style
                .set("max-height", "calc(100vh - 120px)")
                .set("opacity", "1")
                .set("pointer-events", "auto")

            titleIcon.isVisible = true
            headerLabel.isVisible = true
            toggleIcon.isVisible = true

        } else {
            width = "56px"
            minWidth = "56px"
            maxWidth = "56px"

            contentWrapper.style
                .set("max-height", "0")
                .set("opacity", "0")
                .set("pointer-events", "none")

            titleIcon.isVisible = true   // BLEIBT sichtbar (wichtig)
            headerLabel.isVisible = false
            toggleIcon.isVisible = false
        }
    }

    private fun sectionCard(title: String, content: VerticalLayout): VerticalLayout {
        val heading = Span(title).apply {
            style
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.08em")
                .set("color", "#94a3b8")
        }

        return VerticalLayout(heading, content).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            style
                .set("gap", "10px")
                .set("padding", "14px")
                .set("box-sizing", "border-box")
                .set("background", "rgba(255,255,255,0.82)")
                .set("border", "1px solid rgba(148,163,184,0.24)")
                .set("border-radius", "14px")
                .set("box-shadow", "0 4px 16px rgba(15,23,42,0.06)")
                .set("backdrop-filter", "blur(14px)")
        }
    }

    private fun overviewContent(tasks: List<TaskDTO>): VerticalLayout {
        val done = tasks.count { it.state == State.COMPLETE }
        val open = tasks.count { it.state != State.COMPLETE }
        val total = tasks.size
        val progress = if (total > 0) (done.toDouble() / total * 100).toInt() else 0

        val barFill = Div().apply {
            style
                .set("width", "${progress}%")
                .set("height", "100%")
                .set("background", "linear-gradient(90deg, #3b82f6, #6366f1)")
                .set("border-radius", "999px")
                .set("transition", "width 0.4s ease")
        }

        val bar = Div(barFill).apply {
            setWidthFull()
            style
                .set("height", "6px")
                .set("background", "#e2e8f0")
                .set("border-radius", "999px")
                .set("overflow", "hidden")
        }

        val progressLabel = HorizontalLayout(
            Span("$done/$total erledigt").apply {
                style.set("font-size", "12px").set("color", "#64748b")
            },
            Span("$progress%").apply {
                style.set("font-size", "12px").set("color", "#3b82f6").set("font-weight", "700")
            }
        ).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
        }

        return VerticalLayout(
            statRow("Gesamt", total.toString(), "#0f172a"),
            statRow("Offen", open.toString(), "#f59e0b"),
            statRow("Abgeschlossen", done.toString(), "#22c55e"),
            bar,
            progressLabel
        ).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
        }
    }

    private fun statusContent(tasks: List<TaskDTO>): VerticalLayout {
        val rows = State.entries.map { state ->
            val count = tasks.count { it.state == state }
            val color = when (state) {
                State.OPEN -> "#64748b"
                State.TODO -> "#f59e0b"
                State.IN_PROGRESS -> "#3b82f6"
                State.IN_REVIEW -> "#8b5cf6"
                State.COMPLETE -> "#22c55e"
            }
            statRow(state.displayName, count.toString(), color)
        }

        return VerticalLayout(*rows.toTypedArray()).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
        }
    }

    private fun riskContent(tasks: List<TaskDTO>): VerticalLayout {
        data class RiskInfo(val label: String, val color: String, val risk: Risk)

        val risks = listOf(
            RiskInfo("Niedrig", "#3b82f6", Risk.LOW),
            RiskInfo("Mittel", "#f59e0b", Risk.MEDIUM),
            RiskInfo("Hoch", "#ef4444", Risk.HIGH),
        )

        val rows = risks.map { info ->
            val count = tasks.count { it.risk == info.risk }
            statRow(info.label, count.toString(), info.color)
        }

        return VerticalLayout(*rows.toTypedArray()).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
        }
    }

    private fun statRow(label: String, value: String, valueColor: String): HorizontalLayout {
        return HorizontalLayout(
            Span(label).apply {
                style
                    .set("font-size", "13px")
                    .set("color", "#64748b")
                    .set("font-weight", "500")
            },
            Span(value).apply {
                style
                    .set("font-size", "13px")
                    .set("font-weight", "700")
                    .set("color", valueColor)
            }
        ).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
        }
    }
}