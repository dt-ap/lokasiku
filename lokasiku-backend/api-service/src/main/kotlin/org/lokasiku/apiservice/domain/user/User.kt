package org.lokasiku.apiservice.domain.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.lokasiku.apiservice.model.BaseModel
import javax.persistence.Column
import javax.persistence.Entity

@Entity
@JsonIgnoreProperties("id", "passwordDigest", "createdAt", "updatedAt")
class User(
    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var passwordDigest: String,

    @Column(nullable = false)
    var name: String,
) : BaseModel()