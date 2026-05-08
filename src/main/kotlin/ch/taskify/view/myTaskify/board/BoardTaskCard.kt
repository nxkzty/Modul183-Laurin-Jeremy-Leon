package ch.taskify.view.myTaskify.board

import ch.taskify.dto.TaskDTO
import ch.taskify.dto.UserDTO
import ch.taskify.service.task.TaskService
import ch.taskify.view.myTaskify.TaskDialog
import ch.taskify.view.myTaskify.TaskBadges
import ch.taskify.view.myTaskify.ViewTaskDialog
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.dnd.DragSource
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class BoardTaskCard(
    private val task: TaskDTO,
    private val taskService: TaskService,
    private val users: List<UserDTO>,
    private val currentUsername: String,
    private val onRefresh: () -> Unit,
    private val onDragStart: () -> Unit,
    private val onDragEnd: () -> Unit,
) : VerticalLayout() {

    private val createdAtFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN)

    init {
        setWidthFull()
        isPadding = false
        isSpacing = false
        style
            .set("gap", "12px")
            .set("padding", "16px")
            .set("box-sizing", "border-box")
            .set("background", "#ffffff")
            .set("border", "1px solid rgba(148, 163, 184, 0.28)")
            .set("border-radius", "12px")
            .set("box-shadow", "0 10px 26px rgba(15, 23, 42, 0.08)")
            .set("cursor", "grab")
            .set("transition", "transform 0.16s ease, box-shadow 0.16s ease, border-color 0.16s ease")

        element.addEventListener("mouseover") {
            style
                .set("transform", "translateY(-2px)")
                .set("box-shadow", "0 16px 34px rgba(15, 23, 42, 0.12)")
                .set("border-color", "rgba(59, 130, 246, 0.38)")
        }
        element.addEventListener("mouseout") {
            style
                .set("transform", "translateY(0)")
                .set("box-shadow", "0 10px 26px rgba(15, 23, 42, 0.08)")
                .set("border-color", "rgba(148, 163, 184, 0.28)")
        }
        element.addEventListener("dblclick") {
            ViewTaskDialog(
                task = task,
                onEdit = { openEditDialog(task) }
            ).open()
        }

        add(header(), description(), footer())
        configureDragSource()
    }

    private fun openEditDialog(task: TaskDTO) {
        TaskDialog(
            taskService = taskService,
            users = users,
            currentUsername = currentUsername,
            onSave = onRefresh,
            existingTask = task
        ).open()
    }

    private fun header(): VerticalLayout {
        val badges = HorizontalLayout(TaskBadges.riskBadge(task.risk)).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
        }

        val title = Span(task.title).apply {
            style
                .set("color", "#0f172a")
                .set("font-size", "15px")
                .set("font-weight", "700")
                .set("line-height", "1.35")
                .set("overflow-wrap", "anywhere")
        }

        return VerticalLayout(badges, title).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "10px")
        }
    }

    private fun description(): Span {
        val text = task.description.ifBlank { "Keine Beschreibung" }
        return Span(text.take(140).let { if (text.length > 140) "$it..." else it }).apply {
            style
                .set("color", "#64748b")
                .set("font-size", "13px")
                .set("line-height", "1.55")
                .set("overflow-wrap", "anywhere")
        }
    }

    private fun footer(): HorizontalLayout {
        val assignee = task.assigneeUsername ?: "Nicht zugewiesen"
        val avatar = Avatar(assignee).apply {
            name = assignee
            setWidth("28px")
            setHeight("28px")
        }

        val assigneeLabel = Span(assignee).apply {
            style
                .set("color", "#475569")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap")
        }

        val createdAtLabel = Span(formatCreatedAt(task.createdAt)).apply {
            style
                .set("color", "#94a3b8")
                .set("font-size", "12px")
                .set("font-weight", "500")
                .set("white-space", "nowrap")
        }

        val user = HorizontalLayout(avatar, assigneeLabel).apply {
            isPadding = false
            isSpacing = false
            alignItems = FlexComponent.Alignment.CENTER
            style
                .set("gap", "8px")
                .set("min-width", "0")
        }

        return HorizontalLayout(user, createdAtLabel).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
            style
                .set("gap", "12px")
                .set("padding-top", "4px")
        }
    }

    private fun formatCreatedAt(createdAt: LocalDateTime?): String {
        return createdAt?.let { "Erstellt ${it.format(createdAtFormatter)}" } ?: "Erstellt unbekannt"
    }

    private fun configureDragSource() {
        DragSource.create(this).apply {
            addDragStartListener {
                style
                    .set("cursor", "grabbing")
                    .set("opacity", "0.72")
                onDragStart()
            }
            addDragEndListener {
                style
                    .set("cursor", "grab")
                    .set("opacity", "1")
                onDragEnd()
            }
        }
    }
}