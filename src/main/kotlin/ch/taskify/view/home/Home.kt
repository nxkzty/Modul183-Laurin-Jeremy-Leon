package ch.taskify.view.home

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll

@Route("")
@AnonymousAllowed
class Home : VerticalLayout() {
    @PostConstruct
    fun init() {
        add("Hello World")
        val btn = Button("Hello World").apply {
            addClickListener {
                Dialog("Hello").open()
            }
        }
        add(btn)
    }
}
