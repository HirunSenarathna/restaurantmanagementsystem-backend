package com.sdp.userservice.service;

import com.sdp.userservice.dto.Response.UserResponse;
import com.sdp.userservice.entity.Customer;
import com.sdp.userservice.entity.Employee;
import com.sdp.userservice.entity.User;

import com.sdp.userservice.entity.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    void deleteUser(Long id);

    List<UserResponse> getAllUsersByRole(Role role);
}
