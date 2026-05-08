package ch.taskify.entity.task

enum class Risk(val displayName: String) {
    LOW("Niedrig"),
    MEDIUM("Mittel"),
    HIGH("Hoch");

    override fun toString(): String = displayName
}