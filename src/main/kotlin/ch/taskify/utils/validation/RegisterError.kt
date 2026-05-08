package ch.taskify.utils.validation

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

class RegisterError(
    titleText: String = "Registrieren fehlgeschlagen",
    messageText: String = "Benutzername oder Passwort ist falsch"
) : Div() {

    private val title = Span(titleText)
    private val message = Span(messageText)

    init {
        isVisible = false

        style
            .set("background-color", "#fdecea")
            .set("color", "#b71c1c")
            .set("padding", "12px 16px")
            .set("border-radius", "6px")
            .set("margin-bottom", "16px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "10px")

        val icon = Icon(VaadinIcon.WARNING)
        icon.style.set("color", "#d32f2f")

        title.style.set("font-weight", "bold")

        val textContainer = Div(title, message)
        textContainer.style
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "2px")

        add(icon, textContainer)
    }

    fun show(messageText: String) {
        message.text = messageText
        isVisible = true
    }

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}