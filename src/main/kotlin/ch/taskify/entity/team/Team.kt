package ch.taskify.entity.team

import ch.taskify.entity.BaseEntity
import ch.taskify.entity.task.Task
import ch.taskify.entity.user.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class Team : BaseEntity() {

    var name: String? = null

    var description: String? = null

    @OneToMany(fetch = FetchType.LAZY)
    var tasks: MutableSet<Task> = mutableSetOf()

}