package ch.taskify.utils.dialog

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

abstract class TADialog(
    titleText: String,
    subtitleText: String? = null
) : Dialog() {

    private val contentLayout = VerticalLayout()
    private val buttonLayout = HorizontalLayout()
    private val rootLayout = VerticalLayout()

    init {
        width = "600px"

        val title = Span(titleText).apply {
            element.style
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "600")
        }

        val subtitle = subtitleText?.let {
            Span(it).apply {
                element.style
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)")
            }
        }

        contentLayout.apply {
            setPadding(false)
            setSpacing(true)
        }

        buttonLayout.apply {
            setSpacing(true)
            style.set("margin-top", "16px")
        }

        val header = VerticalLayout().apply {
            setPadding(false)
            setSpacing(false)
            add(title)
            subtitle?.let { add(it) }
        }

        rootLayout.apply {
            setPadding(true)
            setSpacing(true)
            add(header, contentLayout, buttonLayout)
        }

        super.add(rootLayout)
    }

    protected fun addContent(vararg components: Component) {
        contentLayout.add(*components)
    }

    protected fun setButtons(vararg buttons: Button) {
        buttonLayout.removeAll()
        buttonLayout.add(*buttons)
    }

    protected fun primary(button: Button): Button =
        button.apply { addThemeVariants(ButtonVariant.LUMO_PRIMARY) }

    protected fun closeButton(text: String = "Abbrechen"): Button =
        Button(text) { close() }

    fun setDialogWidth(width: String) {
        this.width = width
    }

    fun setDialogHeight(height: String) {
        this.height = height
    }

    fun setDialogSize(width: String, height: String) {
        this.width = width
        this.height = height
    }

    fun setPadding(enabled: Boolean) {
        rootLayout.setPadding(enabled)
    }

    fun setSpacing(enabled: Boolean) {
        rootLayout.setSpacing(enabled)
    }
}