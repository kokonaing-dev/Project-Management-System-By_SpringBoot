package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.repository.IssueRepository;
import com.demo.project_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssueRepository issueRepository;

    // this method is working for spring security section
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Email : "+ email);
        // Retrieve the user entity from the database using the email
        User user = userRepository.findByEmail(email);
        // Check if the user exists
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        Set<GrantedAuthority> authorities = user.getAuthorities()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toSet());
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(long id){
        return userRepository.findUserById(id);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }



    public User save(User user) {
        userRepository.save(user);
        return user;
    }

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getUsersByAuthorities(String roleProjectManager, String roleMember) {
        return userRepository.findByAuthoritiesAuthorityIn(List.of(roleProjectManager, roleMember));
    }

    public List<User> getUsersByAuthority(String roleMember) {
        return userRepository.findByAuthoritiesAuthority(roleMember);
    }

    // Method to find users by their IDs
    public Set<User> findByIdIn(List<Long> userIds) {
        return userRepository.findByIdIn(userIds);
    }

    public List<User> getUsersByProjectId(long projectId) {
        // Implement logic to fetch users based on projectId from the database
        return userRepository.findUsersByProjectId(projectId);
    }

    @Transactional
    public Set<Project> findActiveProjectsByUserId(long userId) {
        return userRepository.findActiveProjectsByUserId(userId);
    }

    public int findNumberOfIssuesByUserId(int userId) {
        return userRepository.findNumberOfIssuesByUserId(userId);
    }


    // Method to check if a user with the given username or email already exists
    public boolean isUserExists(String username, String email) {
        // Check if a user with the given username exists
        User existingUserByUsername = userRepository.findByUsername(username);
        if (existingUserByUsername != null) {
            return true; // Username exists
        }

        // Check if a user with the given email exists
        User existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail != null) {
            return true; // Email exists
        }

        return false; // Neither username nor email exists
    }


    public List<User> getAllActiveUsers() {
        return userRepository.findAllActiveUsers();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByAuthoritiesAndStatus(Set<String> authorities, String status) {
        return userRepository.findByAuthoritiesAuthorityInAndStatus(authorities, status);
    }

    public List<User> getUsersByAuthorityAndStatus(String authority, String status) {
        return userRepository.findByAuthoritiesAuthorityAndStatus(authority, status);
    }

    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }
    @jakarta.transaction.Transactional
    public void updateUserPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(newPassword);

            user.setPassword(encodedPassword);
            userRepository.save(user);
            System.out.println("Password updated successfully for user: " + email);
        } else {
            System.out.println("User with email " + email + " not found.");
        }
    }

    public int getTotalAssignedUsersByProjectId(Long projectId) {
        return userRepository.countUsersByProjectsId(projectId);
    }

    public Set<Project> getActiveProjectsByUser(User user) {
        Set<Project> activeProjects = new HashSet<>();

        // Iterate through the user's projects and filter out the active ones
        for (Project project : user.getProjects()) {
            if ("ACTIVE".equals(project.getStatus())) {
                activeProjects.add(project);
            }
        }
        return activeProjects;
    }


    public void updateUserProfile(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User updatedUser) {
        // Retrieve the user from the database by ID
        User existingUser = userRepository.findById(updatedUser.getId()).orElse(null);

        if (existingUser != null) {
            // Update the user's details with the new values
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            // Set the user's role or any other details that need to be updated

            // Save the updated user entity back to the database
            userRepository.save(existingUser);
        } else {
            // Handle the case where the user with the given ID is not found
            throw new RuntimeException("User not found with ID: " + updatedUser.getId());
        }
    }
}


