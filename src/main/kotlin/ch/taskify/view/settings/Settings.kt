package ch.taskify.view.settings

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll

@Route("settings")
@PageTitle("MyTaskify")
@PermitAll
class Settings : VerticalLayout() {

    @PostConstruct
    fun init() {
        add("Hello Settings")
    }

}