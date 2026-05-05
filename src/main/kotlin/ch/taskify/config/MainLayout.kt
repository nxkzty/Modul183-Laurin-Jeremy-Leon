package ch.taskify.config

import ch.taskify.utils.avatar.AvatarBuilder
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
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
            text = "Taskify"
            style.set("font-size", "22px")
            style.set("font-weight", "600")
        }
//
//    private fun createAvatar(): Avatar {
//        val username = authenticationContext.principalName.orElse("User") ?: "User"
//        return Avatar(username.uppercase()).apply {
//            style.set("cursor", "pointer")
//        }
//    }
//
//    private fun createAvatarMenu(avatar: Avatar) {
//        ContextMenu(avatar).apply {
//            isOpenOnClick = true
//
//            addItem(createMenuItem(VaadinIcon.COG, "Settings")) {
//                ui.ifPresent { it.navigate("settings") }
//
//            }
//
//            addItem(createMenuItem(VaadinIcon.SIGN_OUT, "Logout")) {
//                authenticationContext.logout()
//                ui.ifPresent { it.navigate("/") }
//            }
//        }
//    }
//
//    private fun createMenuItem(iconType: VaadinIcon, text: String): HorizontalLayout {
//        val icon = Icon(iconType).apply {
//            setSize(ICON_SIZE)
//        }
//
//        val label = Span(text).apply {
//            style.set("font-size", FONT_SIZE)
//        }
//
//        return HorizontalLayout(icon, label).apply {
//            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
//        }
//    }
}
