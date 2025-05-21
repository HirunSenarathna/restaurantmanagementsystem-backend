package com.sdp.userservice.controller;

import com.sdp.userservice.dto.Request.EmployeeRegistrationRequest;
import com.sdp.userservice.dto.Response.EmployeeResponse;
import com.sdp.userservice.entity.Role;
import com.sdp.userservice.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/register")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<EmployeeResponse> registerEmployee(@Valid @RequestBody EmployeeRegistrationRequest registrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.registerEmployee(registrationRequest));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('OWNER') or hasRole('WAITER') or hasRole('CASHIER') or authentication.principal.username == @employeeService.getEmployeeById(#id).username")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/role/{role}")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByRole(@PathVariable Role role) {
        return ResponseEntity.ok(employeeService.getEmployeesByRole(role));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('OWNER') or hasRole('WAITER') or hasRole('CASHIER') or authentication.principal.username == @employeeService.getEmployeeById(#id).username")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRegistrationRequest updateRequest) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, updateRequest));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('OWNER') or hasRole('WAITER') or hasRole('CASHIER') ")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
