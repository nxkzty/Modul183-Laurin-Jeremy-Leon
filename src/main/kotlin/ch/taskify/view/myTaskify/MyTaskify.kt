package ch.taskify.ui

import ch.taskify.dto.TaskDTO
import ch.taskify.service.task.TaskService
import ch.taskify.service.user.UserService
import ch.taskify.view.myTaskify.CreateTaskDialog
import ch.taskify.view.myTaskify.TasksGrid
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll
import org.springframework.security.core.context.SecurityContextHolder

@Route("myTaskify")
@PageTitle("MyTaskify")
@PermitAll
class MyTaskify(
    private val taskService: TaskService,
    private val userService: UserService,
) : VerticalLayout() {

    private lateinit var grid: Grid<TaskDTO>

    @PostConstruct
    fun init() {
        setSizeFull()
        buildHeader()
        buildContent()
    }

    private fun buildHeader() {
        val title = H1("Meine Aufgaben").apply {
            style.set("margin", "0")
        }

        val subtitle = Span("Deine aktuellen Tasks").apply {
            style.set("color", "#6b7280")
            style.set("font-size", "14px")
        }

        val textBlock = VerticalLayout(title, subtitle).apply {
            isPadding = false
            isSpacing = false
        }

        val createButton = Button("Neue Aufgabe", Icon(VaadinIcon.PLUS)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener {
                val createTaskDialog = CreateTaskDialog(
                    taskService,
                    userService.findAll(),
                    onSave = { refreshGrid() },
                    currentUsername = SecurityContextHolder.getContext().authentication?.name ?: "Unknown"
                )
                createTaskDialog.open()
            }
        }

        val filterButton = Button("Filter", Icon(VaadinIcon.FILTER)).apply {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        }

        val actions = HorizontalLayout(filterButton, createButton).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
        }

        val header = HorizontalLayout(textBlock, actions).apply {
            width = "100%"
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER

            style.set("padding", "16px 0")
            style.set("border-bottom", "1px solid #e5e7eb")
            style.set("margin-bottom", "16px")
        }

        add(header)
    }

    private fun buildContent() {

        grid = TasksGrid(
            taskService = taskService,
            onRefresh = { refreshGrid() },
            onEdit = { task -> (task) }
        )

        refreshGrid()

        if (taskService.getAll().isEmpty()) {
            add(Span("Keine Aufgaben vorhanden"))
        } else {
            add(grid)
        }
    }

    private fun refreshGrid() {
        grid.setItems(taskService.getAll())
    }
}