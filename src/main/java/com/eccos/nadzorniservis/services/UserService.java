package com.eccos.nadzorniservis.services;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eccos.nadzorniservis.models.UserModel;

public interface UserService extends JpaRepository<UserModel, Integer> {
    
    UserModel findByUsername(String username);
    List<UserModel> findAllByRole(String role);
}
