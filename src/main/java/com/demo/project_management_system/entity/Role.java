package com.demo.project_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;


@Data
@Entity
@RequiredArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    private String authority;
//    // Custom Argument Constructor
//    // Lombok generates an all-argument constructor by default if no constructor is provided
//    // Here, a custom constructor is provided to initialize 'authority'
    public Role(String authority) {
        this.authority = authority;
    }

    // Additional methods or overrides can be added here
}
