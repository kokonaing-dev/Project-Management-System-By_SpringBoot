package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Role;
import com.demo.project_management_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository ;


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


}
