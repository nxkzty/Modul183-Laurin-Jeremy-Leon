package ch.taskify.view.home

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct

@Route("/")
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