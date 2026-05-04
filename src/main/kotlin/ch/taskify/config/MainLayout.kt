package ch.taskify.config

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.router.Layout
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll

@Layout
@PermitAll
class MainLayout: AppLayout() {
    @PostConstruct
    fun init() {
        addToNavbar(Button("Navbar"))
        addToDrawer(Button("Drawer"))
    }
}
