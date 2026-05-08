package ch.taskify.entity.task

enum class State(val displayName: String) {
    OPEN("Offen"),
    TODO("Zu erledigen"),
    IN_PROGRESS("In Bearbeitung"),
    IN_REVIEW("In Prüfung"),
    COMPLETE("Abgeschlossen");

    override fun toString(): String = displayName
}