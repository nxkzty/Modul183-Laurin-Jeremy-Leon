package ch.taskify.view.login

import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext

@Route("login", autoLayout = false)
@AnonymousAllowed
class LoginView(private val authenticationContext: AuthenticationContext) : VerticalLayout(), BeforeEnterObserver {

    private val login = LoginForm()

    init {
        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER

        login.action = "login"

        add(login)
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        if (authenticationContext.isAuthenticated) {
            event.forwardTo("myTaskify")
            return
        }


        if (event.location.queryParameters.parameters.containsKey("error")) {
            login.isError = true
            login.setI18n(LoginI18n.createDefault().apply {
                errorMessage.title = "Login fehlgeschlagen"
                errorMessage.message = "Benutzername oder Passwort ist falsch"
            })
        }
    }

}
