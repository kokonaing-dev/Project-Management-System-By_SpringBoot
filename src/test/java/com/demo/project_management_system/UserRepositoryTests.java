//
//package com.demo.project_management_system;
//
//import com.demo.project_management_system.entity.User;
//import com.demo.project_management_system.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.annotation.Rollback;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(value = false)
//public class UserRepositoryTests {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    public void testCreateUser(){
//        User user = new User();
//        user.setUsername("Aung Aung");
//        user.setEmail("aungaung@gmail.com");
//        user.setPassword("$2a$10$pvU1MDzTtdbsuEV6JDMl1eZNfsOEea5lorHaP24C3DFYDJN5cTMii");
//
//        User savedUser = userRepository.save(user);
//
//        User existUser = entityManager.find(User.class, savedUser.getId());
//
//        assertEquals(user.getUsername(), existUser.getUsername());
//        assertEquals(user.getEmail(), existUser.getEmail());
//        assertEquals(user.getPassword(), existUser.getPassword());
//    }
//
//    @Test
//    public void testFindUserByEmail(){
//        String email = "aungaung@gmail.com";
//        User user = userRepository.findByEmail(email);
//
//        assertNotNull(user);
//    }
//}
