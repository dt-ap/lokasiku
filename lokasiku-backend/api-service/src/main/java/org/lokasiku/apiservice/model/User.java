package org.lokasiku.apiservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class User extends BaseModel {
  @Column
  private String email;

  @Column(nullable = false)
  private String passwordDigest;

  @Column(nullable = false)
  private String name;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordDigest() {
    return passwordDigest;
  }

  public void setPasswordDigest(String passwordDigest) {
    this.passwordDigest = passwordDigest;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}