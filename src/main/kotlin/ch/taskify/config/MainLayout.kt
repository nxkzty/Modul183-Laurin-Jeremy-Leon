package ch.taskify.config

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.spring.security.AuthenticationContext
import com.vaadin.flow.router.Layout
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll

@Layout
@PermitAll
class MainLayout(
    private val authenticationContext: AuthenticationContext
) : AppLayout() {
    @PostConstruct
    fun init() {
        val title = Div().apply {
            text = "Taskify"
        }
        val username = authenticationContext.principalName.orElse("User") ?: "User"
        val avatar = Avatar(username.uppercase())
        val navbar = HorizontalLayout(title, avatar).apply {
            setWidthFull()
            expand(title)
            defaultVerticalComponentAlignment = com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER
            isPadding = true
            isSpacing = true
        }

        addToNavbar(navbar)
        addToDrawer(Button("Drawer"))
    }
}
