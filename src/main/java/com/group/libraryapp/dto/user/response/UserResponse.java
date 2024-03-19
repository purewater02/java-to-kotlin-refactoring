package com.group.libraryapp.dto.user.response;

import com.group.libraryapp.user.User;

public class UserResponse {

  private final long id;
  private final String name;
  private final Integer age;

  public UserResponse(User User) {
    this.id = User.getId();
    this.name = User.getName();
    this.age = User.getAge();
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getAge() {
    return age;
  }

}
