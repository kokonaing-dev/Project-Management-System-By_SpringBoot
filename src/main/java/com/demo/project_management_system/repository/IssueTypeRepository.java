package com.demo.project_management_system.repository;

import com.demo.project_management_system.entity.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {


}
