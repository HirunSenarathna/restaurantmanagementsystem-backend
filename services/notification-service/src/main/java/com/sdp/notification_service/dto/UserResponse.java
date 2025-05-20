package com.sdp.notification_service.dto;


import com.sdp.notification_service.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String address;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
