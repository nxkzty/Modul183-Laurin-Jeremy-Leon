package ch.taskify.view.myTaskify.board

import ch.taskify.dto.TaskDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.task.State
import ch.taskify.service.task.TaskService
import ch.taskify.service.user.UserService
import ch.taskify.utils.CurrentUser
import ch.taskify.utils.notify.Notify
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route("myTaskify/board")
@PageTitle("Board")
@PermitAll
class Board(
    private val taskService: TaskService,
    private val userService: UserService,
) : VerticalLayout() {

    private val boardContent = HorizontalLayout()
    private val outerLayout = HorizontalLayout()
    private var draggedTask: TaskDTO? = null
    private val columns = mutableListOf<BoardColumn>()
    private var statsSidebar: BoardStatsSidebar? = null
    private val users: List<UserDTO> by lazy { userService.findAll() }

    init {
        setSizeFull()
        isPadding = false
        isSpacing = false
        buildHeader()
        buildBoard()
    }

    private fun buildHeader() {
        val title = H1("Board").apply {
            style
                .set("margin", "0")
                .set("font-size", "clamp(22px, 4vw, 32px)")
                .set("font-weight", "700")
                .set("color", "#111827")
        }

        val subtitle =
            Span("Alle Aufgaben nach Status. Ziehe Karten zwischen die Spalten, um den Status zu ändern.").apply {
                style
                    .set("color", "#64748b")
                    .set("font-size", "14px")
            }

        val textBlock = VerticalLayout(title, subtitle).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "4px")
        }

        add(
            HorizontalLayout(textBlock).apply {
                setWidthFull()
                defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
                style
                    .set("padding", "clamp(16px, 3vw, 28px) clamp(16px, 3vw, 32px) 20px")
                    .set("box-sizing", "border-box")
            }
        )
    }

    private fun buildBoard() {
        boardContent.apply {
            setHeightFull()
            isPadding = false
            isSpacing = false
            width = "0"
            element.style.set("flex", "1 1 auto")
            style
                .set("gap", "16px")
                .set("align-items", "stretch")
                .set("flex-wrap", "nowrap")
                .set("overflow-x", "auto")
                .set("overflow-y", "visible")
                .set("min-width", "0")
        }

        outerLayout.apply {
            setWidthFull()
            setHeightFull()
            isPadding = false
            isSpacing = false
            style
                .set("display", "flex")
                .set("flex-direction", "row")
                .set("gap", "16px")
                .set("padding", "0 clamp(16px, 3vw, 32px) 32px")
                .set("box-sizing", "border-box")
                .set("align-items", "stretch")
                .set("overflow", "hidden")
        }

        statsSidebar = BoardStatsSidebar(loadBoardTasks())
        outerLayout.add(boardContent, statsSidebar)

        add(outerLayout)
        expand(outerLayout)
        refreshBoard()
    }

    private fun refreshBoard() {
        val tasks = loadBoardTasks()
        val tasksByState = tasks.groupBy { it.state }

        boardContent.removeAll()
        columns.clear()

        statsSidebar?.let { outerLayout.remove(it) }
        statsSidebar = BoardStatsSidebar(tasks)
        outerLayout.add(statsSidebar)

        State.entries.forEach { state ->
            val column = BoardColumn(
                state = state,
                tasks = tasksByState[state].orEmpty(),
                onDrop = { targetState -> moveDraggedTaskTo(targetState) },
                cardFactory = { task -> createTaskCard(task) }
            )
            columns.add(column)
            boardContent.add(column)
        }
    }

    private fun loadBoardTasks(): List<TaskDTO> = taskService.getAll()

    private fun createTaskCard(task: TaskDTO): Component {
        return BoardTaskCard(
            task = task,
            taskService = taskService,
            users = users,
            currentUsername = CurrentUser.name,
            onRefresh = { refreshBoard() },
            onDragStart = {
                draggedTask = task
                columns.forEach { it.highlightDropZone() }
            },
            onDragEnd = {
                draggedTask = null
                columns.forEach { it.clearHighlight() }
            }
        )
    }

    private fun moveDraggedTaskTo(targetState: State) {
        val task = draggedTask ?: return
        if (task.state == targetState) return

        val updatedTask = task.copy(state = targetState)
        taskService.update(task.id!!, updatedTask)
        draggedTask = null

        columns.forEach { it.clearHighlight() }
        refreshBoard()

        Notify.success("Status auf ${targetState.displayName} geändert.")
    }
}