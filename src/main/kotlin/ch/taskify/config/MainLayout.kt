package ch.taskify.config

import ch.taskify.utils.avatar.AvatarBuilder
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.Layout
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext
import jakarta.annotation.PostConstruct

@Layout
@AnonymousAllowed
class MainLayout(
    private val avatarBuilder: AvatarBuilder,
    private val authenticationContext: AuthenticationContext
) : AppLayout() {

    companion object {
        private const val ICON_SIZE = "14px"
        private const val FONT_SIZE = "13px"
    }

    @PostConstruct
    fun init() {
        createNavbar()
    }

    private fun createNavbar() {
        val title = createTitle()
        val isLoggedIn = authenticationContext.principalName.isPresent

        val rightSlot = if (isLoggedIn) {
            avatarBuilder.createAvatarWithMenu()
        } else {
            createAuthButtons()
        }

        val navbar = HorizontalLayout(title, rightSlot).apply {
            setWidthFull()
            expand(title)
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isPadding = true
            isSpacing = true
        }

        addToNavbar(navbar)
    }

    private fun createAuthButtons(): HorizontalLayout {
        val loginButton = Button("Anmelden").apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener { UI.getCurrent().navigate("login") }
        }

        val registerButton = Button("Registrieren").apply {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            addClickListener { UI.getCurrent().navigate("register") }
        }

        return HorizontalLayout(registerButton, loginButton).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
            isPadding = false
        }
    }

    private fun createTitle(): Div =
        Div().apply {
            val text = Span("Taskify").apply {
                style.set("font-size", "28px")
                style.set("font-weight", "600")
                style.set("letter-spacing", "-0.5px")
            }

            val accent = Span(".").apply {
                style.set("color", "var(--lumo-primary-color)")
                style.set("font-size", "28px")
                style.set("font-weight", "600")
            }

            add(text, accent)

            style.set("display", "flex")
            style.set("align-items", "center")
            style.set("gap", "2px")

            style.set("cursor", "pointer")

            addClickListener {
                ui.ifPresent { it.navigate("myTaskify") }
            }
        }
}
