package ch.taskify.view.registration

import ch.taskify.dto.UserDTO
import ch.taskify.entity.user.Role
import ch.taskify.service.user.UserService
import ch.taskify.utils.validation.RegisterError
import ch.taskify.utils.validation.UserValidator
import ch.taskify.view.login.LoginView
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import java.util.UUID

@Route("register")
@AnonymousAllowed
class RegistrationView(
    private val userService: UserService
) : VerticalLayout() {


    private var name = TextField("Name")
    private var password = PasswordField("Passwort")
    private var confirmPassword = PasswordField("Passwort verifizieren")
    private var binder = Binder(UserDTO::class.java)
    private val registerButton = Button("Registrieren")
    private var userValidator: UserValidator = UserValidator(
        binder,
        nameField = name,
        passwordField = password,
        confirmPasswordField = confirmPassword
    )
    private val errorMessage = RegisterError()

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
                val isValid = userValidator.validate(errorMessage)
                if (isValid) {
                    val userDTO = UserDTO(
                        UUID.randomUUID(),
                        name.value.trim(),
                        password.value.trim(),
                        Role.USER
                    )
                    handleSignUp(userDTO)
                }
            }
        }

        val form = VerticalLayout(
            createTitle(),
            errorMessage,
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
        userService.createUser(signUp.name, signUp.passwordHash, signUp.role)
        UI.getCurrent().navigate(LoginView::class.java)
    }

}