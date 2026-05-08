package ch.taskify.utils.validation

import ch.taskify.dto.UserDTO
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder

class UserValidator(
    private val binder: Binder<UserDTO>,
    private val nameField: TextField,
    private val passwordField: PasswordField,
    private val confirmPasswordField: PasswordField
) {

    init {

        binder.forField(nameField)
            .withValidator(
                { !it.isNullOrBlank() },
                "Name ist erforderlich"
            )
            .bind(UserDTO::name, UserDTO::name::set)

        binder.forField(passwordField)
            .withValidator(
                { !it.isNullOrBlank() },
                "Passwort ist erforderlich"
            )
            .withValidator(
                { it.length >= 9 },
                "Passwort muss mindestens 9 Zeichen haben"
            )
            .withValidator(
                { it.any(Char::isUpperCase) },
                "Passwort muss mindestens 1 Grossbuchstaben enthalten"
            )
            .withValidator(
                { password ->
                    password.any { !it.isLetterOrDigit() }
                },
                "Passwort muss mindestens 1 Sonderzeichen enthalten"
            )
            .bind(UserDTO::passwordHash, UserDTO::passwordHash::set)

        binder.forField(confirmPasswordField)
            .withValidator(
                { !it.isNullOrBlank() },
                "Passwort verifizieren ist erforderlich"
            )
            .withValidator(
                { confirmPassword ->
                    confirmPassword == passwordField.value
                },
                "Passwörter müssen übereinstimmen"
            )
            .bind( UserDTO::passwordHash, UserDTO::passwordHash::set)

    }

    fun validate(errorMessage: RegisterError): Boolean {
        errorMessage.hide()

        val result = binder.validate()

        if (!result.isOk) {
            errorMessage.show("Bitte Eingaben prüfen")
        }

        return result.isOk
    }
}