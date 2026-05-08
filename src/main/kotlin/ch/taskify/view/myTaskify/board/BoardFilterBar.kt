package ch.taskify.view.myTaskify.board

import ch.taskify.dto.UserDTO
import ch.taskify.entity.task.Risk
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class BoardFilterBar(
    users: List<UserDTO>,
    private val onFilterChange: (
        search: String,
        risks: Set<Risk>,
        assignees: Set<String>
    ) -> Unit,
) : HorizontalLayout() {

    private val searchField = TextField()
    private val riskFilter = MultiSelectComboBox<Risk>()
    private val assigneeFilter = MultiSelectComboBox<String>()

    private val filterContainer = HorizontalLayout()
    private var expanded = false

    init {
        setWidthFull()
        isPadding = false
        isSpacing = false
        alignItems = FlexComponent.Alignment.START

        style
            .set("gap", "14px")
            .set("padding", "0 clamp(16px, 3vw, 32px) 22px")
            .set("box-sizing", "border-box")
            .set("flex-wrap", "wrap")

        add(buildToggleHeader())

        buildSearch()
        buildRiskFilter()
        buildAssigneeFilter(users)

        val resetButton = buildResetButton()

        filterContainer.apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            alignItems = FlexComponent.Alignment.CENTER

            style
                .set("gap", "14px")
                .set("padding", "16px")
                .set("background", "rgba(255,255,255,0.72)")
                .set("border", "1px solid rgba(148,163,184,0.18)")
                .set("border-radius", "18px")
                .set("backdrop-filter", "blur(14px)")
                .set("box-shadow", "0 12px 32px rgba(15,23,42,0.06)")
                .set("flex-wrap", "wrap")
        }

        filterContainer.add(
            searchField,
            riskFilter,
            assigneeFilter,
            resetButton
        )

        filterContainer.isVisible = false

        add(filterContainer)
    }

    private fun buildToggleHeader(): HorizontalLayout {

        val icon = Icon(VaadinIcon.FILTER).apply {
            setSize("16px")
        }
        val text = com.vaadin.flow.component.html.Span("Filter")

        return HorizontalLayout(icon, text).apply {

            alignItems = FlexComponent.Alignment.CENTER
            isSpacing = true

            style
                .set("cursor", "pointer")
                .set("font-weight", "600")
                .set("gap", "8px")
                .set("padding", "8px 12px")
                .set("border-radius", "12px")
                .set("background", "rgba(241,245,249,0.6)")

            addClickListener {

                expanded = !expanded
                filterContainer.isVisible = expanded

            }
        }
    }

    private fun buildSearch() {
        searchField.placeholder = "Aufgaben suchen..."
        searchField.prefixComponent = Icon(VaadinIcon.SEARCH)
        searchField.isClearButtonVisible = true

        searchField.style.set("min-width", "280px")
        searchField.element.style.set("border-radius", "14px")

        searchField.addValueChangeListener { fireFilterChanged() }
    }

    private fun buildRiskFilter() {
        riskFilter.setItems(Risk.entries)

        riskFilter.setItemLabelGenerator {
            when (it) {
                Risk.LOW -> "Niedrig"
                Risk.MEDIUM -> "Mittel"
                Risk.HIGH -> "Hoch"
            }
        }

        riskFilter.placeholder = "Risiken auswählen"
        riskFilter.style.set("min-width", "240px")
        riskFilter.element.style.set("border-radius", "14px")

        riskFilter.addValueChangeListener { fireFilterChanged() }
    }

    private fun buildAssigneeFilter(users: List<UserDTO>) {
        assigneeFilter.setItems(users.map { it.name }.sorted())

        assigneeFilter.placeholder = "Mitarbeiter auswählen"
        assigneeFilter.style.set("min-width", "320px")
        assigneeFilter.element.style.set("border-radius", "14px")

        assigneeFilter.addValueChangeListener { fireFilterChanged() }
    }

    private fun buildResetButton(): Button {
        return Button("Reset", Icon(VaadinIcon.CLOSE_SMALL)).apply {

            addThemeVariants(ButtonVariant.LUMO_TERTIARY)

            style
                .set("height", "42px")
                .set("border-radius", "14px")
                .set("padding", "0 16px")
                .set("font-weight", "600")

            addClickListener {
                searchField.clear()
                riskFilter.clear()
                assigneeFilter.clear()
                fireFilterChanged()
            }
        }
    }

    private fun fireFilterChanged() {
        onFilterChange(
            searchField.value ?: "",
            riskFilter.selectedItems,
            assigneeFilter.selectedItems
        )
    }
}