package ch.taskify.entity

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.UUID

@MappedSuperclass
class BaseEntity {
    @Id
    var id: UUID? = null

    @LastModifiedDate
    var updatedAt: Instant = Instant.now()

    @CreatedDate
    var createdAt: Instant = Instant.now()
}