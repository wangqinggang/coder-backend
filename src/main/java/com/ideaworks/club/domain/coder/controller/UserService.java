package com.ideaworks.club.domain.coder.controller;

import java.util.List;

public interface UserService {
  User saveUser(User user);

  List<User> getAllUsers();

  User getUserByAge(Integer age);

  User updateUserByAge(User user, Integer age);

  Void deleteUserByAge(Integer age);

  Boolean isUserExist(Integer age);
}