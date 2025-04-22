//package com.sdp.userservice.mapper;
//
//import com.sdp.userservice.entity.Customer;
//import com.sdp.userservice.entity.Employee;
//import com.sdp.userservice.entity.User;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UserMapper {
//
//    public UserDto toDto(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        if (user instanceof Customer) {
//            return toCustomerDto((Customer) user);
//        } else if (user instanceof Employee) {
//            return toEmployeeDto((Employee) user);
//        } else {
//            return new UserDto(
//                    user.getId(),
//                    user.getKeycloakId(),
//                    user.getUsername(),
//                    user.getFirstName(),
//                    user.getLastName(),
//                    user.getEmail(),
//                    user.getPhone(),
//                    user.getAddress(),
//                    user.getRole(),
//                    user.getCreatedAt(),
//                    user.getUpdatedAt()
//            );
//        }
//    }
//
//    public CustomerDto toCustomerDto(Customer customer) {
//        if (customer == null) {
//            return null;
//        }
//
//        CustomerDto dto = new CustomerDto();
//        dto.setId(customer.getId());
//        dto.setKeycloakId(customer.getKeycloakId());
//        dto.setUsername(customer.getUsername());
//        dto.setFirstName(customer.getFirstName());
//        dto.setLastName(customer.getLastName());
//        dto.setEmail(customer.getEmail());
//        dto.setPhone(customer.getPhone());
//        dto.setAddress(customer.getAddress());
//        dto.setRole(customer.getRole());
//        dto.setCreatedAt(customer.getCreatedAt());
//        dto.setUpdatedAt(customer.getUpdatedAt());
//        dto.setLoyaltyPoints(customer.getLoyaltyPoints());
//        dto.setLastVisited(customer.getLastVisited());
//
//        return dto;
//    }
//
//    public EmployeeDto toEmployeeDto(Employee employee) {
//        if (employee == null) {
//            return null;
//        }
//
//        EmployeeDto dto = new EmployeeDto();
//        dto.setId(employee.getId());
//        dto.setKeycloakId(employee.getKeycloakId());
//        dto.setUsername(employee.getUsername());
//        dto.setFirstName(employee.getFirstName());
//        dto.setLastName(employee.getLastName());
//        dto.setEmail(employee.getEmail());
//        dto.setPhone(employee.getPhone());
//        dto.setAddress(employee.getAddress());
//        dto.setRole(employee.getRole());
//        dto.setCreatedAt(employee.getCreatedAt());
//        dto.setUpdatedAt(employee.getUpdatedAt());
//        dto.setDateOfBirth(employee.getDateOfBirth());
//        dto.setIdCardNumber(employee.getIdCardNumber());
//
//        return dto;
//    }
//
//    public Customer toCustomerEntity(UserRegistrationRequest request) {
//        if (request == null) {
//            return null;
//        }
//
//        Customer customer = new Customer();
//        customer.setUsername(request.getUsername());
//        customer.setFirstName(request.getFirstName());
//        customer.setLastName(request.getLastName());
//        customer.setEmail(request.getEmail());
//        customer.setPhone(request.getPhone());
//        customer.setAddress(request.getAddress());
//        customer.setRole(request.getRole());
//        customer.setLoyaltyPoints(0);
//
//        return customer;
//    }
//
//    public Employee toEmployeeEntity(UserRegistrationRequest request) {
//        if (request == null) {
//            return null;
//        }
//
//        Employee employee = new Employee();
//        employee.setUsername(request.getUsername());
//        employee.setFirstName(request.getFirstName());
//        employee.setLastName(request.getLastName());
//        employee.setEmail(request.getEmail());
//        employee.setPhone(request.getPhone());
//        employee.setAddress(request.getAddress());
//        employee.setRole(request.getRole());
//        employee.setDateOfBirth(request.getDateOfBirth());
//        employee.setIdCardNumber(request.getIdCardNumber());
//
//        return employee;
//    }
//
//    public void updateUserFromRequest(User user, UserUpdateRequest request) {
//        if (request.getFirstName() != null) {
//            user.setFirstName(request.getFirstName());
//        }
//        if (request.getLastName() != null) {
//            user.setLastName(request.getLastName());
//        }
//        if (request.getEmail() != null) {
//            user.setEmail(request.getEmail());
//        }
//        if (request.getPhone() != null) {
//            user.setPhone(request.getPhone());
//        }
//        if (request.getAddress() != null) {
//            user.setAddress(request.getAddress());
//        }
//
//        if (user instanceof Employee && request.getDateOfBirth() != null) {
//            ((Employee) user).setDateOfBirth(request.getDateOfBirth());
//        }
//        if (user instanceof Employee && request.getIdCardNumber() != null) {
//            ((Employee) user).setIdCardNumber(request.getIdCardNumber());
//        }
//    }
//}
