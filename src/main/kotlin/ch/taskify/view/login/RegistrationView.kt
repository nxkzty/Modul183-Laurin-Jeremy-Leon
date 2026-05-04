package ch.taskify.view.login

import ch.taskify.dto.UserDTO
import ch.taskify.entity.user.Role
import ch.taskify.service.user.UserService
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

@Route("register", autoLayout = false)
@AnonymousAllowed
class RegistrationView(
    private val userService: UserService
) : VerticalLayout() {


    private val name = TextField("Name")
    private val password = PasswordField("Passwort")
    private val confirmPassword = PasswordField("Passwort verifizieren")
    private val registerButton = Button("Registrieren")

    init {
        configureLayout()
        buildForm()
    }

    private fun configureLayout() {
        setSizeFull()
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    }

    private fun buildForm() {
        name.applyFullWidth()
        password.applyFullWidth()
        confirmPassword.applyFullWidth()


        registerButton.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            width = "100%"
            addClickListener {
                val userDTO = UserDTO(
                    name.value.trim(),
                    password.value.trim(),
                )
                handleSignUp(userDTO)
            }
        }

        val form = VerticalLayout(
            createTitle(),
            name,
            password,
            confirmPassword,
            registerButton
        ).apply {
            width = "320px"
            isPadding = false
            isSpacing = true
        }

        add(form)
    }

    private fun createTitle(): H2 {
        return H2("Registrieren").apply {
            addClassName("register-title")
        }
    }

    private fun HasSize.applyFullWidth() {
        width = "100%"
    }

    private fun handleSignUp(signUp: UserDTO) {
        userService.createUser(signUp.name, signUp.passwordHash, Role.USER)
        UI.getCurrent().navigate(LoginView::class.java)
    }

}