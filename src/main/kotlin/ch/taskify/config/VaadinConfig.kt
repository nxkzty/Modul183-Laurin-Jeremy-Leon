package ch.taskify.config

import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.ColorScheme
import com.vaadin.flow.theme.aura.Aura
import com.vaadin.flow.theme.lumo.Lumo
import java.awt.Color


@StyleSheet(Lumo.STYLESHEET)
@ColorScheme(ColorScheme.Value.LIGHT)
class VaadinConfig: AppShellConfigurator