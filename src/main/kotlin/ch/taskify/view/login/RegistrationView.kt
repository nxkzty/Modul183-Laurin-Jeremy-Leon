package ch.taskify.view.login

import ch.taskify.view.home.Home
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

/*
 * RegistrationView.java  
 *
 * Creator:
 * 04.05.2026 13:01 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 13:01 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */
@Route("register", autoLayout = false)
@AnonymousAllowed
class RegistrationView : VerticalLayout() {


    private val email = TextField("Username")
    private val password = PasswordField("Password")
    private val confirmPassword = PasswordField("Confirm Password")
    private val registerButton = Button("Register")

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
        email.applyFullWidth()
        password.applyFullWidth()
        confirmPassword.applyFullWidth()

        registerButton.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            width = "100%"
            addClickListener {
                println("register")
            }
        }

        val form = VerticalLayout(
            createTitle(),
            email,
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
        return H2("Register").apply {
            addClassName("register-title")
        }
    }

    private fun HasSize.applyFullWidth() {
        width = "100%"
    }

}