package org.lokasiku.apiservice.config

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy

class PhysicalNamingStrategy : SpringPhysicalNamingStrategy() {

    override fun toPhysicalTableName(name: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier {
        // Warning. Hacky implementation to determine table name.
        val newName = when(name?.text?.get(0)?.isUpperCase()) {
            true ->  "${super.toPhysicalTableName(name, jdbcEnvironment).text}s"
            false, null -> name?.text
        }
        return Identifier.toIdentifier(newName)
    }
}