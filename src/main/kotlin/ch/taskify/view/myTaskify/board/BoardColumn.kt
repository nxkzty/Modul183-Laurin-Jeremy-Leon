package ch.taskify.view.myTaskify.board

import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.State
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dnd.DropTarget
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class BoardColumn(
    private val state: State,
    tasks: List<TaskDTO>,
    private val onDrop: (State) -> Unit,
    private val cardFactory: (TaskDTO) -> Component,
) : VerticalLayout() {

    private val cards = VerticalLayout()

    init {
        setHeightFull()
        minWidth = "260px"
        width = "0"
        element.style.set("flex", "1 1 0")
        isPadding = false
        isSpacing = false
        style
            .set("background", "rgba(255, 255, 255, 0.82)")
            .set("border", "1px solid rgba(148, 163, 184, 0.24)")
            .set("border-radius", "14px")
            .set("box-shadow", "0 18px 45px rgba(15, 23, 42, 0.08)")
            .set("overflow", "hidden")
            .set("backdrop-filter", "blur(14px)")

        add(header(tasks.size), cardsContainer(tasks))
        configureDropTarget()
    }

    private fun header(taskCount: Int): HorizontalLayout {
        val title = Span(state.displayName).apply {
            style
                .set("font-size", "14px")
                .set("font-weight", "700")
                .set("color", "#0f172a")
        }

        val count = Span(taskCount.toString()).apply {
            style
                .set("min-width", "26px")
                .set("height", "24px")
                .set("border-radius", "999px")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background", "#e2e8f0")
                .set("color", "#475569")
                .set("font-size", "12px")
                .set("font-weight", "700")
        }

        return HorizontalLayout(title, count).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
            style
                .set("padding", "16px")
                .set("box-sizing", "border-box")
                .set("border-bottom", "1px solid rgba(148, 163, 184, 0.2)")
        }
    }

    private fun cardsContainer(tasks: List<TaskDTO>): VerticalLayout {
        cards.apply {
            setWidthFull()
            setHeightFull()
            minHeight = "200px"
            isPadding = false
            isSpacing = false
            style
                .set("gap", "12px")
                .set("padding", "14px")
                .set("box-sizing", "border-box")
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
        }

        if (tasks.isEmpty()) {
            cards.add(emptyState())
        } else {
            tasks.forEach { cards.add(cardFactory(it)) }
        }

        return cards
    }

    private fun emptyState(): Div {
        return Div(Span("Keine Aufgaben")).apply {
            setWidthFull()
            style
                .set("height", "88px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("border", "1px dashed #cbd5e1")
                .set("border-radius", "10px")
                .set("color", "#94a3b8")
                .set("font-size", "13px")
                .set("font-weight", "600")
        }
    }

    private fun configureDropTarget() {
        DropTarget.create(cards).apply {
            addDropListener {
                clearHighlight()
                onDrop(state)
            }
        }
    }

    fun highlightDropZone() {
        cards.style
            .set("background", "#e0f2fe")
            .set("border", "2px dashed #38bdf8")
    }

    fun clearHighlight() {
        cards.style
            .remove("background")
            .remove("border")
    }
}
