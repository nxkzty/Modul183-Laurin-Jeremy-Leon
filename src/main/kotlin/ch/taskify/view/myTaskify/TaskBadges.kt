package ch.taskify.view.myTaskify

import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import com.vaadin.flow.component.html.Span

object TaskBadges {

    private val STATE_COLORS = mapOf(
        State.OPEN        to Triple("Offen",           "#e5e7eb", "#6b7280"),
        State.TODO        to Triple("Zu erledigen",           "#dbeafe", "#3b82f6"),
        State.IN_PROGRESS to Triple("In Bearbeitung",  "#fef3c7", "#f59e0b"),
        State.IN_REVIEW   to Triple("In Review",       "#ede9fe", "#8b5cf6"),
        State.COMPLETE    to Triple("Abgeschlossen",   "#d1fae5", "#10b981")
    )

    private val RISK_COLORS = mapOf(
        Risk.LOW    to Triple("Niedriges Risiko", "#d1fae5", "#10b981"),
        Risk.MEDIUM to Triple("Mittleres Risiko", "#fef3c7", "#f59e0b"),
        Risk.HIGH   to Triple("Hohes Risiko",     "#fee2e2", "#ef4444")
    )

    fun stateBadge(state: State): Span {
        val (label, bg, color) = STATE_COLORS[state] ?: Triple(state.name, "#f3f4f6", "#9ca3af")
        return badge(label, bg, color)
    }

    fun riskBadge(risk: Risk?): Span {
        val (label, bg, color) = risk?.let { RISK_COLORS[it] } ?: Triple("Kein Risiko", "#f3f4f6", "#9ca3af")
        return badge(label, bg, color)
    }

    private fun badge(label: String, bg: String, color: String): Span =
        Span(label).apply {
            style
                .set("background", bg)
                .set("color", color)
                .set("padding", "2px 10px")
                .set("border-radius", "12px")
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
        }
}