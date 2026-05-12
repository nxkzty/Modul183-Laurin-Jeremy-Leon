package ch.taskify.view.settings

import ch.taskify.service.user.UserService
import ch.taskify.utils.CurrentUser
import ch.taskify.utils.notify.Notify
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll

@Route("settings")
@PageTitle("Settings")
@PermitAll
class Settings(
    private val userService: UserService
) : VerticalLayout() {

    private val username = TextField("Name")
    private val role = TextField("Rolle")
    private val password = PasswordField("Neues Passwort")
    private val confirmPassword = PasswordField("Neues Passwort bestätigen")

    @PostConstruct
    fun init() {
        setSizeFull()
        isPadding = false
        isSpacing = false

        val currentUserId = CurrentUser.principalAsUserEntity?.id
        if (currentUserId == null) {
            add(Span("Kein Benutzer geladen."))
            return
        }

        val user = userService.findById(currentUserId)
        if (user == null) {
            add(Span("Benutzer konnte nicht geladen werden."))
            return
        }

        username.value = user.name
        role.value = user.role.displayName
        role.isReadOnly = true

        add(buildCenteredLayout(user.name))
    }

    private fun buildCenteredLayout(name: String): VerticalLayout {

        val header = buildHeader(name)
        val card = buildCard()

        return VerticalLayout(header, card).apply {
            setSizeFull()

            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER

            setSpacing(true)

            style
                .set("padding", "24px")
                .set("box-sizing", "border-box")
        }
    }

    /**
     * HEADER (Avatar + Title + Subtitle)
     */
    private fun buildHeader(name: String): VerticalLayout {

        val avatar = Avatar(name).apply {
            width = "54px"
            height = "54px"
        }

        val title = H1("Settings").apply {
            style
                .set("margin", "0")
                .set("font-size", "clamp(22px, 4vw, 32px)")
                .set("font-weight", "700")
                .set("color", "var(--lumo-header-text-color)")
        }

        val subtitle = Span("Profil und Login-Daten bearbeiten").apply {
            style
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "14px")
        }

        return VerticalLayout(avatar, title, subtitle).apply {
            isPadding = false
            isSpacing = true

            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER

            style
                .set("text-align", "center")
        }
    }

    /**
     * SETTINGS CARD
     */
    private fun buildCard(): VerticalLayout {

        username.isRequiredIndicatorVisible = true
        username.width = "100%"
        role.width = "100%"
        password.width = "100%"
        confirmPassword.width = "100%"

        val form = FormLayout(username, role, password, confirmPassword).apply {
            responsiveSteps = listOf(
                FormLayout.ResponsiveStep("0", 1),
                FormLayout.ResponsiveStep("640px", 2)
            )
            setWidthFull()
        }

        val saveButton = Button("Speichern") {
            saveCurrentUser()
        }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }

        return VerticalLayout(form, saveButton).apply {
            width = "100%"
            maxWidth = "760px"

            isPadding = true
            isSpacing = true

            defaultHorizontalComponentAlignment = FlexComponent.Alignment.STRETCH

            style
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "14px")
                .set("box-shadow", "0 8px 24px var(--lumo-contrast-10pct)")
                .set("box-sizing", "border-box")
        }
    }

    /**
     * SAVE LOGIC
     */
    private fun saveCurrentUser() {

        val currentUserId = CurrentUser.principalAsUserEntity?.id ?: return
        val normalizedUsername = username.value.trim()
        val newPassword = password.value

        if (normalizedUsername.isBlank()) {
            Notify.error("Name ist erforderlich.")
            return
        }

        if (newPassword.isNotBlank() && !isPasswordValid(newPassword)) {
            Notify.error("Passwort braucht 9 Zeichen, einen Grossbuchstaben und ein Sonderzeichen.")
            return
        }

        if (newPassword.isNotBlank() && newPassword != confirmPassword.value) {
            Notify.error("Passwörter müssen übereinstimmen.")
            return
        }

        try {
            val updatedUser = userService.updateUser(
                id = currentUserId,
                username = normalizedUsername,
                rawPassword = newPassword.takeIf { it.isNotBlank() }
            )

            CurrentUser.principalAsUserEntity?.name = updatedUser.name

            password.value = ""
            confirmPassword.value = ""

            Notify.success("User gespeichert.")
        } catch (exception: IllegalArgumentException) {
            Notify.error("User konnte nicht gespeichert werden.")
        }
    }

    private fun isPasswordValid(value: String): Boolean {
        return value.length >= 9 &&
                value.any(Char::isUpperCase) &&
                value.any { !it.isLetterOrDigit() }
    }
}
