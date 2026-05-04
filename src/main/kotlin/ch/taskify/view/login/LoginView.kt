package ch.taskify.view.login

import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
class LoginView : VerticalLayout() {

    private val login = LoginForm()

    init {
        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER

        login.action = "login"

        add(login)
    }
}