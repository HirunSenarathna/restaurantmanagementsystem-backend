package com.sdp.userservice.service.impl;

import com.sdp.userservice.dto.Request.EmployeeRegistrationRequest;
import com.sdp.userservice.dto.Response.EmployeeResponse;
import com.sdp.userservice.entity.Employee;
import com.sdp.userservice.entity.Role;
import com.sdp.userservice.repository.EmployeeRepository;
import com.sdp.userservice.repository.UserRepository;
import com.sdp.userservice.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmployeeResponse registerEmployee(EmployeeRegistrationRequest request) {
        // Validate role is not CUSTOMER
        if (request.getRole() == Role.CUSTOMER) {
            throw new IllegalArgumentException("Employee role cannot be CUSTOMER");
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if ID card number already exists
        if (employeeRepository.existsByIdCardNumber(request.getIdCardNumber())) {
            throw new IllegalArgumentException("ID card number already exists");
        }

        // Create and save new employee
        Employee employee = Employee.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .dateOfBirth(request.getDateOfBirth())
                .idCardNumber(request.getIdCardNumber())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        return mapToEmployeeResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        return mapToEmployeeResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToEmployeeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByRole(Role role) {
        if (role == Role.CUSTOMER) {
            throw new IllegalArgumentException("Role cannot be CUSTOMER for employees");
        }

        return employeeRepository.findByRole(role).stream()
                .map(this::mapToEmployeeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRegistrationRequest request) {
        // Validate role is not CUSTOMER
        if (request.getRole() == Role.CUSTOMER) {
            throw new IllegalArgumentException("Employee role cannot be CUSTOMER");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        // Check if username is being changed and is already taken
        if (!employee.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email is being changed and is already taken
        if (!employee.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if ID card number is being changed and is already taken
        if (!employee.getIdCardNumber().equals(request.getIdCardNumber()) &&
                employeeRepository.existsByIdCardNumber(request.getIdCardNumber())) {
            throw new IllegalArgumentException("ID card number already exists");
        }

        // Update employee details
        employee.setFirstname(request.getFirstname());
        employee.setLastname(request.getLastname());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setUsername(request.getUsername());
        employee.setRole(request.getRole());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setIdCardNumber(request.getIdCardNumber());

        // Only update password if it's provided (not empty)
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        return mapToEmployeeResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private EmployeeResponse mapToEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstname(employee.getFirstname())
                .lastname(employee.getLastname())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .username(employee.getUsername())
                .role(employee.getRole())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .dateOfBirth(employee.getDateOfBirth())
                .idCardNumber(employee.getIdCardNumber())
                .build();
    }
}
