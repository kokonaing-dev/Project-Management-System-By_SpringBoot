package com.demo.project_management_system.repository;

import com.demo.project_management_system.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.project_management_system.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    @Query("SELECT DISTINCT p FROM User u JOIN u.projects p WHERE u.id = :userId AND p.status = 'ACTIVE'")
    Set<Project> findActiveProjectsByUserId(@Param("userId") long userId);

    User findUserById (long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void deleteById(long id);

    List<User> findByAuthoritiesAuthorityIn(List<String> roleProjectManager);

    List<User> findByAuthoritiesAuthority(String roleMember);

    Set<User> findByIdIn(List<Long> userIds);

    @Query("SELECT u FROM User u JOIN u.projects p WHERE p.id = ?1")
    List<User> findUsersByProjectId(long projectId);


    @Query("SELECT COUNT(issue) FROM Issue issue JOIN issue.users user WHERE user.id = :userId")
    int findNumberOfIssuesByUserId(@Param("userId") long userId);

    @Query("SELECT u FROM User u WHERE u.status = 'Active'")
    List<User> findAllActiveUsers();

    List<User> findByAuthoritiesAuthorityInAndStatus(Set<String> authorities, String status);

    List<User> findByAuthoritiesAuthorityAndStatus(String authority, String status);

    int countUsersByProjectsId(Long projectId);
}
