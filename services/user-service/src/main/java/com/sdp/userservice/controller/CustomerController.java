package com.sdp.userservice.controller;

import com.sdp.userservice.dto.Request.CustomerRegistrationRequest;
import com.sdp.userservice.dto.Response.CustomerResponse;
import com.sdp.userservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest registrationRequest) {
        System.out.println(registrationRequest + "in registerCustomer controller");
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.registerCustomer(registrationRequest));
    }

    @GetMapping("/{id}")
   // @PreAuthorize("hasRole('OWNER') or hasRole('CASHIER') or hasRole('CUSTOMER') or authentication.principal.username == @customerService.getCustomerById(#id).username")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    //@PreAuthorize("hasRole('OWNER') or hasRole('CASHIER')")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('OWNER') or hasRole('CUSTOMER') or authentication.principal.username == @customerService.getCustomerById(#id).username")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRegistrationRequest updateRequest) {
        return ResponseEntity.ok(customerService.updateCustomer(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('OWNER') or hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
