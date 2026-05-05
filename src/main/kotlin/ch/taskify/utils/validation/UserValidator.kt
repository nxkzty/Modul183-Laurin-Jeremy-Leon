package ch.taskify.utils.validation

import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField

class UserValidator() {

    fun validate(
        errorMessage: RegisterError,
        nameField: TextField,
        passwordField: PasswordField,
        confirmPasswordField: PasswordField
    ): Boolean {
        var valid = true

        nameField.isInvalid = false
        passwordField.isInvalid = false
        confirmPasswordField.isInvalid = false
        errorMessage.hide()

        val name : String = nameField.value
        val password: String = passwordField.value
        val confirmPassword: String = confirmPasswordField.value

        if (name.isBlank()) {
            nameField.isInvalid = true
            nameField.errorMessage = "Name ist erforderlich"
            valid = false
        }

        if (password.isBlank()) {
            passwordField.isInvalid = true
            passwordField.errorMessage = "Passwort ist erforderlich"
            valid = false
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordField.isInvalid = true
            confirmPasswordField.errorMessage = "Passwort verifizieren ist erforderlich"
            valid = false
        }

        if (valid && password != confirmPassword) {
            confirmPasswordField.isInvalid = true
            confirmPasswordField.errorMessage = "Passwörter müssen übereinstimmen"
            return false
        }

        return valid
    }
}