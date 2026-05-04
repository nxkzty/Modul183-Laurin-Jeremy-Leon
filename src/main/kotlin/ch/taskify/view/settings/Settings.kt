package ch.taskify.view.settings

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct

@Route("settings")
class Settings : VerticalLayout() {

    @PostConstruct
    fun init() {
        add("Hello Settings")
    }

}