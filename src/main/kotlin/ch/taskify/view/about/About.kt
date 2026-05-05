package ch.taskify.view.about

import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

@Route("about")
@PageTitle("Über Uns")
@AnonymousAllowed
class About : VerticalLayout() {

    init {
        setWidthFull()
        maxWidth = "900px"
        setPadding(true)
        setSpacing(true)

        style.set("margin", "0 auto")

        add(
            H1("Projektauftrag WebApp – Applikationssicherheit"),

            Paragraph(
                "Aus diesem Projektauftrag ist Taskify entstanden. " +
                        "Die Anwendung wurde im Rahmen eines schulischen Auftrags entwickelt, " +
                        "bei dem der Fokus nicht auf einer möglichst komplexen Funktionalität lag, " +
                        "sondern auf dem Verständnis und der Umsetzung von Applikationssicherheit."
            ),

            Paragraph(
                "Taskify dient somit als praktische Umsetzung der im Unterricht behandelten Sicherheitskonzepte " +
                        "und wurde gezielt entwickelt, um typische Risiken moderner Webanwendungen sichtbar und behandelbar zu machen."
            ),

            section(
                "Ziel des Projekts",
                "Ihr lernt den praktischen Umgang mit Applikationssicherheit anhand einer selbst entwickelten WebApp. " +
                        "Dabei steht nicht die Grösse oder Komplexität der Anwendung im Vordergrund, sondern die sichere Umsetzung."
            ),

            section(
                "Auftrag",
                "Ihr arbeitet in Dreierteams und entwickelt eine eigene WebApp nach euren Vorstellungen. " +
                        "Diese kann später auch mit anderen Personen geteilt und getestet werden."
            ),

            section(
                "Anforderungen",
                """
                • Eigene WebApp entwickeln  
                • GitHub Account verwenden und registrieren  
                • Template gbs-app00 als Basis nutzen  
                • Fokus auf Applikationssicherheit  
                • Funktionalität dient als Grundlage für Security-Aspekte  
                """.trimIndent()
            ),

            section(
                "Sicherheitsschwerpunkt",
                """
                • A03:2025 – Software Supply Chain Failures (Pflicht)  
                • + 3 weitere OWASP Top 10 Kategorien  
                • Empfehlung: A01 – Broken Access Control  
                
                Ziel ist es, Risiken zu erkennen, passende Schutzmassnahmen umzusetzen und diese nachvollziehbar zu dokumentieren.
                """.trimIndent()
            ),

            section(
                "Beispiele für mögliche Anwendungen",
                """
                • Task Tracker (wie Taskify)  
                • Notiz-App  
                • Twitter / Social Feed Clone  
                • Lernplattform  
                • Rezeptverwaltung  
                • Terminplaner  
                """.trimIndent()
            ),

            section(
                "Vorgehen im Projekt",
                """
                1. Team bilden (3 Personen)  
                2. Idee für WebApp entwickeln  
                3. Mit Lehrperson abstimmen und Go holen  
                4. Dokument erstellen:
                   - Name der WebApp  
                   - Kurzbeschreibung  
                   - Features  
                   - Tech Stack  
                5. Dokument in Moodle hochladen  
                """.trimIndent()
            ),

            section(
                "Bewertung",
                "Bewertet wird nicht der Funktionsumfang der App, sondern wie gut ihr Applikationssicherheit versteht, " +
                        "umsetzt und dokumentiert."
            ),

            section(
                "Ausblick",
                "Nach der Bewertung werden die WebApps gegenseitig getestet. " +
                        "Im Rahmen eines Red Team / Blue Team Szenarios werden Angriffe und Verteidigung praktisch analysiert."
            )
        )
    }

    private fun section(title: String, content: String): VerticalLayout {
        return VerticalLayout(
            H2(title),
            Paragraph(content)
        ).apply {
            setPadding(false)
            setSpacing(false)

            style.set("margin-bottom", "18px")

            getChildren().forEach {
                if (it is Paragraph) {
                    it.style.set("line-height", "1.6")
                    it.style.set("color", "var(--lumo-secondary-text-color)")
                }
            }
        }
    }
}