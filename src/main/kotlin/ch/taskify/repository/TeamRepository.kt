package ch.taskify.repository

import ch.taskify.entity.team.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TeamRepository : JpaRepository<Team, UUID> {
    fun findByNameIgnoreCase(name: String): Team?
    fun existsByNameIgnoreCase(name: String): Boolean

    @Query("select t from UserEntity u join u.teams t where u.id = :userId")
    fun findByUserId(@Param("userId") userId: UUID): List<Team>
}
