package ch.taskify.view.myTaskify.board

import ch.taskify.dto.TaskDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.task.Risk
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

    private val users: List<UserDTO> by lazy {
        userService.findAll()
    }

    private var searchFilter = ""
    private var riskFilter: Set<Risk> = emptySet()
    private var assigneeFilter: Set<String> = emptySet()

    private var isPeopleBoardExpanded = true
    private var isStatsBoardExpanded = true

    init {

        setSizeFull()

        isPadding = false
        isSpacing = false

        style.set(
            "background",
            "linear-gradient(180deg, #f8fafc 0%, #eef2ff 100%)"
        )

        buildHeader()
        buildFilters()
        buildBoard()
    }

    private fun buildHeader() {

        val title = H1("Board").apply {
            style
                .set("margin", "0")
                .set(
                    "font-size",
                    "clamp(22px, 4vw, 32px)"
                )
                .set("font-weight", "700")
                .set("color", "#111827")
        }

        val subtitle = Span(
            "Alle Aufgaben nach Status. Ziehe Karten zwischen die Spalten, um den Status zu ändern."
        ).apply {
            style
                .set("color", "#64748b")
                .set("font-size", "14px")
        }

        val textBlock =
            VerticalLayout(title, subtitle).apply {

                isPadding = false
                isSpacing = false

                style.set("gap", "6px")
            }

        add(
            HorizontalLayout(textBlock).apply {

                setWidthFull()

                defaultVerticalComponentAlignment =
                    FlexComponent.Alignment.CENTER

                style
                    .set(
                        "padding",
                        "clamp(20px, 3vw, 30px) clamp(16px, 3vw, 32px) 20px"
                    )
                    .set("box-sizing", "border-box")
            }
        )
    }

    private fun buildFilters() {

        add(
            BoardFilterBar(
                users = users,
                onFilterChange = {
                        search,
                        risks,
                        assignees ->

                    searchFilter = search
                    riskFilter = risks
                    assigneeFilter = assignees

                    refreshBoard()
                }
            )
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
                .set("gap", "18px")
                .set("align-items", "stretch")
                .set("flex-wrap", "nowrap")
                .set("overflow-x", "auto")
                .set("overflow-y", "hidden")
                .set("min-width", "0")
                .set("padding-bottom", "8px")
        }

        outerLayout.apply {

            setWidthFull()
            setHeightFull()

            isPadding = false
            isSpacing = false

            style
                .set("display", "flex")
                .set("flex-direction", "row")
                .set("gap", "8px")
                .set(
                    "padding",
                    "0 clamp(16px, 3vw, 32px) 32px"
                )
                .set("box-sizing", "border-box")
                .set("align-items", "stretch")
                .set("overflow", "hidden")
        }

        add(outerLayout)

        expand(outerLayout)

        refreshBoard()
    }

    private fun refreshBoard() {

        val tasks = filteredTasks()

        val tasksByState =
            tasks.groupBy { it.state }

        boardContent.removeAll()

        columns.clear()

        outerLayout.removeAll()

        val statsSidebar = BoardStatsSidebar(
            tasks = tasks,
            initiallyExpanded =
                isStatsBoardExpanded,
            onExpandedChange = { expanded ->
                isStatsBoardExpanded = expanded
            }
        )

        val peopleSidebar = BoardPeopleSidebar(
            tasks = tasks,
            initiallyExpanded =
                isPeopleBoardExpanded,
            onExpandedChange = { expanded ->
                isPeopleBoardExpanded = expanded
            }
        )

        val sidebar = VerticalLayout(
            statsSidebar,
            peopleSidebar
        ).apply {

            isPadding = false
            isSpacing = false

            style
                .set("gap", "4px")
                .set("padding-bottom", "8px")
                .set("width", "fit-content")
                .set("min-width", "0")
                .set("flex-shrink", "0")
        }

        outerLayout.add(boardContent, sidebar)

        State.entries.forEach { state ->

            val column = BoardColumn(
                state = state,
                tasks = tasksByState[state].orEmpty(),

                onDrop = { targetState ->
                    moveDraggedTaskTo(targetState)
                },

                cardFactory = { task ->
                    createTaskCard(task)
                }
            )

            columns.add(column)

            boardContent.add(column)
        }
    }

    private fun filteredTasks(): List<TaskDTO> {

        return taskService.getAll()
            .filter {

                val matchesSearch =
                    it.title.contains(
                        searchFilter,
                        true
                    ) ||
                            it.description.contains(
                                searchFilter,
                                true
                            )

                val matchesRisk =
                    riskFilter.isEmpty() ||
                            riskFilter.contains(it.risk)

                val matchesAssignee =
                    assigneeFilter.isEmpty() ||
                            assigneeFilter.contains(
                                it.assigneeUsername
                            )

                matchesSearch &&
                        matchesRisk &&
                        matchesAssignee
            }
    }

    private fun createTaskCard(
        task: TaskDTO
    ): Component {

        return BoardTaskCard(
            task = task,
            taskService = taskService,
            users = users,
            currentUsername = CurrentUser.name,

            onRefresh = {
                refreshBoard()
            },

            onDragStart = {

                draggedTask = task

                columns.forEach {
                    it.highlightDropZone()
                }
            },

            onDragEnd = {

                draggedTask = null

                columns.forEach {
                    it.clearHighlight()
                }
            }
        )
    }

    private fun moveDraggedTaskTo(
        targetState: State
    ) {

        val task = draggedTask ?: return

        if (task.state == targetState) {
            return
        }

        val updatedTask =
            task.copy(state = targetState)

        taskService.update(
            task.id!!,
            updatedTask
        )

        draggedTask = null

        columns.forEach {
            it.clearHighlight()
        }

        refreshBoard()

        Notify.success(
            "Status auf ${targetState.displayName} geändert."
        )
    }
}