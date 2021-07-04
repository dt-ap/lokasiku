package org.lokasiku.apiservice.model

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