package com.demo.project_management_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

    @GetMapping("/admin/test")
    public String showAdmin(){
        return "Admin is here";
    }

    @GetMapping("/member/test")
    public String showMember(){
        return "Member is here";
    }
}
