package ch.taskify.view.home

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext

@Route("")
@AnonymousAllowed
class Home(
    private val authenticationContext: AuthenticationContext
) : VerticalLayout(), BeforeEnterObserver {

    init {
        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER

        add(
            H1("Willkommen bei Taskify"),
            Paragraph("Organisiere deine Aufgaben einfach und effizient."),
            Button("Registrieren") {
                ui.ifPresent { it.navigate("register") }
            },
            Button("Anmelden") {
                ui.ifPresent { it.navigate("login") }
            }
        )
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        if (authenticationContext.isAuthenticated) {
            event.forwardTo("myTaskify")
        }
    }
}
