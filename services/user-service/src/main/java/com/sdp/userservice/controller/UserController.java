package com.sdp.userservice.controller;


import com.sdp.userservice.entity.Employee;
import com.sdp.userservice.entity.User;
import com.sdp.userservice.entity.Role;
import com.sdp.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {



}
