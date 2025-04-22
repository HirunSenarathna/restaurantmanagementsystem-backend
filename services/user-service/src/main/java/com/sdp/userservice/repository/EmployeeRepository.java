package com.sdp.userservice.repository;

import com.sdp.userservice.entity.Employee;
import com.sdp.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByRole(Role role);

    boolean existsByIdCardNumber(String idCardNumber);
}
