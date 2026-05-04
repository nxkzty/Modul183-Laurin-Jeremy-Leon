package ch.taskify.entity.team

import ch.taskify.entity.BaseEntity
import ch.taskify.entity.task.Task
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany

@Entity
class Team : BaseEntity() {

    var name: String? = null

    var description: String? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "team_tasks",
        joinColumns = [JoinColumn(name = "team_id")],
        inverseJoinColumns = [JoinColumn(name = "task_id")]
    )
    var tasks: MutableSet<Task> = mutableSetOf()

}
