package org.lokasiku.apiservice.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@MappedSuperclass
abstract class BaseModel() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @field:CreationTimestamp
    @Column(nullable = false)
    lateinit var createdAt: Instant

    @field:UpdateTimestamp
    @Column(nullable = false)
    lateinit var updatedAt: Instant
}