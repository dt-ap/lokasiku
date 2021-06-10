package org.lokasiku.apiservice.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

public class CustomPhysicalNamingStrategy extends SpringPhysicalNamingStrategy {

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    var newName = super.toPhysicalTableName(name, jdbcEnvironment).getText();
    return Identifier.toIdentifier(newName + "s");
  }

}