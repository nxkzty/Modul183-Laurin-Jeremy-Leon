package ch.taskify.config

import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.ColorScheme
import com.vaadin.flow.server.AppShellSettings
import com.vaadin.flow.theme.lumo.Lumo


@StyleSheet(Lumo.STYLESHEET)
@ColorScheme(ColorScheme.Value.LIGHT)
class VaadinConfig: AppShellConfigurator {

    override fun configurePage(settings: AppShellSettings) {
        settings.addFavIcon(
            "icon",
            "/icons/taskify-512.svg",
            "512x512")
    }
}
