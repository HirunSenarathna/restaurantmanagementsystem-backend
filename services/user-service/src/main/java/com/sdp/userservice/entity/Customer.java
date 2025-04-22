package com.sdp.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "customers")
@DiscriminatorValue("CUSTOMER")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder

public class Customer extends User {

    @Override
    protected void onCreate() {
        super.onCreate();
        setRole(Role.CUSTOMER);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", firstname='" + getFirstname() + '\'' +
                ", lastname='" + getLastname() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
