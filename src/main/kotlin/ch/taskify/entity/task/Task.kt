package ch.taskify.entity.task

import ch.taskify.entity.BaseEntity
import ch.taskify.entity.user.UserEntity
import jakarta.persistence.*

@Entity
class Task : BaseEntity() {

    var title: String = ""

    @Enumerated(EnumType.STRING)
    var state: State = State.OPEN

    var description: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    var assignee: UserEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_id")
    var issuer: UserEntity? = null

    @Enumerated(EnumType.STRING)
    var risk: Risk? = null


}
