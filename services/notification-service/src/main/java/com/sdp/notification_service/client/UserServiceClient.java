package com.sdp.notification_service.client;

import com.sdp.notification_service.dto.UserResponse;
import com.sdp.notification_service.model.UserRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

//    @GetMapping("/api/u/{userId}")
//    Map<String, String> getUserById(@PathVariable("userId") String userId);
//
//    @GetMapping("/api/users/role/{role}")
//    List<Map<String, String>> getUsersByRole(@PathVariable("role") String role);

    @GetMapping("/api/users/id/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/users/role/{role}")
    List<UserResponse> getUsersByRole(@PathVariable("role") UserRole role);
}
