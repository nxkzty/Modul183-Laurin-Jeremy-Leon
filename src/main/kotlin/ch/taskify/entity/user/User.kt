package ch.taskify.entity.user

import ch.taskify.entity.BaseEntity
import ch.taskify.entity.team.Team
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToMany
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
class User: BaseEntity(), UserDetails {
    var name: String = ""

    var passwordHash: String = ""

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER

    @ManyToMany(fetch = FetchType.LAZY)
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