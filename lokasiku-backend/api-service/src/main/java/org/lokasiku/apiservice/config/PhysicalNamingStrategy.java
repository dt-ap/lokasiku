package org.lokasiku.apiservice.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

public class PhysicalNamingStrategy extends SpringPhysicalNamingStrategy {

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    // Warning. Hacky code.
    var isClassName = Character.isUpperCase(name.getText().charAt(0));
    var newName = isClassName ? super.toPhysicalTableName(name, jdbcEnvironment).getText() + "s" : name.getText();
    return Identifier.toIdentifier(newName);
  }

}