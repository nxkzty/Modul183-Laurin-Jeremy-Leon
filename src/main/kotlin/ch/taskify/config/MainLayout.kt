package ch.taskify.config


import ch.taskify.ui.MyTaskify
import ch.taskify.utils.avatar.AvatarBuilder
import ch.taskify.view.about.About
import ch.taskify.view.settings.Settings
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.AfterNavigationObserver
import com.vaadin.flow.router.Layout
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext
import jakarta.annotation.PostConstruct

@Layout
@AnonymousAllowed
class MainLayout(
    private val avatarBuilder: AvatarBuilder,
    private val authenticationContext: AuthenticationContext
) : AppLayout(), AfterNavigationObserver {

    private var navItems: List<NavTabs> = emptyList()
    private var tabs: List<RouterLink> = emptyList()

    @PostConstruct
    fun init() {
        createNavbar()
    }

    override fun afterNavigation(event: com.vaadin.flow.router.AfterNavigationEvent?) {
        highlightActiveTab(event)
    }

    private fun createNavbar() {
        val title = createTitle()
        val isLoggedIn = authenticationContext.principalName.isPresent

        navItems = getNavItems(isLoggedIn)

        tabs = navItems.map { createTab(it) }

        val centerMenu = HorizontalLayout(*tabs.toTypedArray()).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            style.set("gap", "6px")
        }

        val rightSlot = if (isLoggedIn) {
            avatarBuilder.createAvatarWithMenu()
        } else {
            createAuthButtons()
        }

        val navbar = HorizontalLayout(title, centerMenu, rightSlot).apply {
            setWidthFull()
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER

            expand(centerMenu)

            style.set("padding", "10px 16px")
            style.set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
            style.set("background", "var(--lumo-base-color)")
        }

        addToNavbar(navbar)
    }

    private fun getNavItems(isLoggedIn: Boolean): List<NavTabs> {
        return if (isLoggedIn) {
            getLoggedInTabes()
        } else {
            getLoggedOutTabes()
        }
    }

    private fun getLoggedInTabes(): List<NavTabs> {
        return listOf(
            NavTabs("MyTaskify", "/myTaskify", MyTaskify::class.java),
            NavTabs("Settings", "settings", Settings::class.java),
            NavTabs("Über Uns", "about", About::class.java)

        )
    }

    private fun getLoggedOutTabes(): List<NavTabs> {
        return listOf(
            NavTabs("Über Uns", "about", About::class.java)
        )
    }

    private fun createTab(item: NavTabs): RouterLink {
        return RouterLink(item.label, item.view).apply {

            style.set("text-decoration", "none")
            style.set("padding", "8px 12px")
            style.set("border-radius", "8px")
            style.set("color", "var(--lumo-secondary-text-color)")
            style.set("transition", "0.2s ease")

            element.addEventListener("mouseover") {
                style.set("background", "var(--lumo-contrast-5pct)")
            }

            element.addEventListener("mouseout") {
                style.remove("background")
            }
        }
    }

    private fun highlightActiveTab(event: com.vaadin.flow.router.AfterNavigationEvent?) {

        val current = event?.location?.path ?: ""

        val normalized = normalizePath(current)

        tabs.forEachIndexed { index, tab ->

            val route = normalizePath(navItems[index].route)

            val isActive = when {
                route.isEmpty() && normalized.isEmpty() -> true
                route.isNotEmpty() && normalized == route -> true
                else -> false
            }

            if (isActive) {
                tab.style.set("color", "var(--lumo-primary-color)")
                tab.style.set("font-weight", "600")
            } else {
                tab.style.set("color", "var(--lumo-secondary-text-color)")
                tab.style.remove("font-weight")
            }
        }
    }

    private fun normalizePath(path: String): String {
        return path.trim().removePrefix("/")
    }

    private fun createTitle(): RouterLink {

        val link = RouterLink("", MyTaskify::class.java)

        val text = Span("Taskify")
        val dot = Span(".").apply {
            style.set("color", "var(--lumo-primary-color)")
        }

        link.add(text, dot)

        link.style.set("font-size", "22px")
        link.style.set("font-weight", "600")
        link.style.set("text-decoration", "none")
        link.style.set("color", "inherit")

        return link
    }

    private fun createAuthButtons(): HorizontalLayout {

        val login = Button("Anmelden").apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener { UI.getCurrent().navigate("login") }
        }

        val register = Button("Registrieren").apply {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            addClickListener { UI.getCurrent().navigate("register") }
        }

        return HorizontalLayout(register, login).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
        }
    }
}