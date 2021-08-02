package org.lokasiku.apiservice.domain.user

import org.lokasiku.apiservice.model.BaseModel
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class User(
    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var passwordDigest: String,

    @Column(nullable = false)
    var name: String,
) : BaseModel()