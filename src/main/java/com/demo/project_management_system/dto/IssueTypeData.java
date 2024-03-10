package com.demo.project_management_system.dto;

public class IssueTypeData {
    private String name;
    private Long count;

    public IssueTypeData(String name, Long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
// Constructor, getters, and setters
}