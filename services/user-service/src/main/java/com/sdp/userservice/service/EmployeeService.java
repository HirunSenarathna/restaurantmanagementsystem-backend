package com.sdp.userservice.service;

import com.sdp.userservice.dto.Request.EmployeeRegistrationRequest;
import com.sdp.userservice.dto.Response.EmployeeResponse;
import com.sdp.userservice.entity.Role;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse registerEmployee(EmployeeRegistrationRequest registrationRequest);

    EmployeeResponse getEmployeeById(Long id);

    List<EmployeeResponse> getAllEmployees();

    List<EmployeeResponse> getEmployeesByRole(Role role);

    EmployeeResponse updateEmployee(Long id, EmployeeRegistrationRequest updateRequest);

    void deleteEmployee(Long id);
}
