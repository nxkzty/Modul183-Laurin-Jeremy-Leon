package ch.taskify.ui

import ch.taskify.dto.TaskDTO
import ch.taskify.service.task.TaskService
import ch.taskify.service.user.UserService
import ch.taskify.utils.CurrentUser
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
        isPadding = false
        isSpacing = false
        buildHeader()
        buildContent()
    }

    private fun buildHeader() {
        val title = H1("Meine Aufgaben").apply {
            style
                .set("margin", "0")
                .set("font-size", "clamp(22px, 4vw, 32px)")
                .set("font-weight", "700")
                .set("color", "#111827")
        }

        val subtitle = Span("Deine zugewiesenen Aufgaben").apply {
            style
                .set("color", "#64748b")
                .set("font-size", "14px")
        }

        val textBlock = VerticalLayout(title, subtitle).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "4px")
        }

        val createButton = Button("Neue Aufgabe", Icon(VaadinIcon.PLUS)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener {
                val createTaskDialog = CreateTaskDialog(
                    taskService,
                    userService.findAll(),
                    onSave = { refreshGrid() },
                    currentUsername = CurrentUser.name
                )
                createTaskDialog.open()
            }
        }

        val filterButton = Button("Filter", Icon(VaadinIcon.FILTER)).apply {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        }

        val actions = HorizontalLayout(filterButton, createButton).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
        }

        val header = HorizontalLayout(textBlock, actions).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            style
                .set("padding", "clamp(16px, 3vw, 28px) clamp(16px, 3vw, 32px) 20px")
                .set("box-sizing", "border-box")
        }

        add(header)
    }

    private fun buildContent() {
        grid = TasksGrid(taskService, userService.findAll(), CurrentUser.name, onRefresh = { refreshGrid() })
        refreshGrid()

        val contentWrapper = VerticalLayout().apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            style
                .set("padding", "0 clamp(16px, 3vw, 32px) 32px")
                .set("box-sizing", "border-box")
        }

        if (taskService.getAll().isEmpty()) {
            contentWrapper.add(Span("Keine Aufgaben vorhanden"))
        } else {
            contentWrapper.add(grid)
        }

        add(contentWrapper)
    }

    private fun refreshGrid() {
        grid.setItems(taskService.getAllFromCurrentUser())
    }
}