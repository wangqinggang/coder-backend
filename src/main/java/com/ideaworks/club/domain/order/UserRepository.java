package com.ideaworks.club.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ideaworks.club.domain.coder.controller.User;

public interface UserRepository extends JpaRepository<User, String> {
}
