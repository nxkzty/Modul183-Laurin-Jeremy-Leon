package ch.taskify.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.UUID

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity {
    @Id
    @GeneratedValue
    var id: UUID? = null

    @LastModifiedDate
    var updatedAt: Instant = Instant.now()

    @CreatedDate
    var createdAt: Instant = Instant.now()
}
