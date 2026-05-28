package ch.taskify.entity.user

enum class Role(val displayName: String) {
    USER("Teammitglied"),
    ADMIN("Administrator");

    override fun toString(): String = displayName

}
