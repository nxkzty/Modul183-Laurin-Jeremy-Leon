package ch.taskify.entity.task

import ch.taskify.entity.BaseEntity
import ch.taskify.entity.user.User
import jakarta.persistence.*

@Entity
class Task : BaseEntity() {

    var title: String = ""

    @Enumerated(EnumType.STRING)
    var state: State = State.OPEN

    var description: String = ""

    @OneToOne(fetch = FetchType.LAZY)
    var assignee: User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    var issuer: User? = null

    @Enumerated(EnumType.STRING)
    var risk: Risk? = null


}