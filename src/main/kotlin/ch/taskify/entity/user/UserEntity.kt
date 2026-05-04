package ch.taskify.entity.user

import ch.taskify.entity.BaseEntity
import ch.taskify.entity.team.Team
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "user_entity")
class UserEntity: BaseEntity(), UserDetails {
    var name: String = ""

    var passwordHash: String = ""

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_entity_teams",
        joinColumns = [JoinColumn(name = "user_entity_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    var teams: MutableSet<Team> = mutableSetOf()

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String? {
        return passwordHash
    }

    override fun getUsername(): String {
        return name
    }


}
