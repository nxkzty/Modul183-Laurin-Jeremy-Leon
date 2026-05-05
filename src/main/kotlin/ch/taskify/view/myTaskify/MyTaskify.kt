package ch.taskify.ui

import ch.taskify.dto.TaskDTO
import ch.taskify.entity.task.Risk
import ch.taskify.entity.task.State
import ch.taskify.entity.task.Task
import ch.taskify.service.task.TaskService
import ch.taskify.service.user.UserService
import ch.taskify.view.myTaskify.CreateTaskDialog
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.*
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
                    onSave = { refreshGrid() }
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

        grid = Grid(TaskDTO::class.java, false).apply {
            width = "100%"
            addThemeVariants(GridVariant.LUMO_ROW_STRIPES)

            addColumn(TaskDTO::title)
                .setHeader("Titel")
                .setAutoWidth(true)
                .setFlexGrow(1)

            addColumn { task ->
                if (task.description.length > 50)
                    task.description.take(50) + "..."
                else
                    task.description
            }.setHeader("Beschreibung")
                .setFlexGrow(2)

            addComponentColumn { task ->
                Span(task.state.name).apply {
                    style.set("background-color", getStateColor(task.state))
                    style.set("color", "white")
                    style.set("padding", "4px 10px")
                    style.set("border-radius", "999px")
                    style.set("font-size", "12px")
                }
            }.setHeader("Status")

            addComponentColumn { task ->
                val text = task.risk?.name ?: "-"
                Span(text).apply {
                    style.set("color", getRiskColor(task.risk))
                    style.set("font-weight", "600")
                }
            }.setHeader("Risiko")

            addColumn { it.assigneeUsername?: "-" }
                .setHeader("Zuständig")

            addColumn { it.issuerUsername?: "-" }
                .setHeader("Erstellt von")

            addComponentColumn { task ->
                val edit = Button(Icon(VaadinIcon.EDIT)).apply {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    addClickListener {
                        // TODO Edit Dialog
                    }
                }

                val delete = Button(Icon(VaadinIcon.TRASH)).apply {
                    addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY)
                    addClickListener {
                        ConfirmDialog(
                            "Löschen?",
                            "Willst du diesen Task wirklich löschen?",
                            "Löschen",
                            {
                                taskService.delete(task.id!!)
                                refreshGrid()
                            },
                            "Abbrechen",
                            {}
                        ).open()
                    }
                }

                HorizontalLayout(edit, delete).apply {
                    isPadding = false
                    isSpacing = true
                }
            }.setHeader("")
        }

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

    private fun getStateColor(state: State): String {
        return when (state) {
            State.OPEN -> "#6b7280"
            State.TODO -> "#3b82f6"
            State.IN_PROGRESS -> "#f59e0b"
            State.IN_REVIEW -> "#8b5cf6"
            State.COMPLETE -> "#10b981"
        }
    }

    private fun getRiskColor(risk: Risk?): String {
        return when (risk) {
            Risk.LOW -> "#10b981"
            Risk.MEDIUM -> "#f59e0b"
            Risk.HIGH -> "#ef4444"
            null -> "#9ca3af"
        }
    }
}