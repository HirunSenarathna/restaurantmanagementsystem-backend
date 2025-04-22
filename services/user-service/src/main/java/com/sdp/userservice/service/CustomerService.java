package com.sdp.userservice.service;

import com.sdp.userservice.dto.Request.CustomerRegistrationRequest;
import com.sdp.userservice.dto.Response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse registerCustomer(CustomerRegistrationRequest registrationRequest);

    CustomerResponse getCustomerById(Long id);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomer(Long id, CustomerRegistrationRequest updateRequest);

    void deleteCustomer(Long id);
}
