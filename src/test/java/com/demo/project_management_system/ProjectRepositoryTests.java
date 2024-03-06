//package com.demo.project_management_system;
//
//import com.demo.project_management_system.entity.Project;
//import com.demo.project_management_system.repository.ProjectRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.annotation.Rollback;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(value = false)
//public class ProjectRepositoryTests {
//    @Autowired
//    private ProjectRepository projectRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    public void testCreateProject(){
//        Project project = new Project();
//        project.setProjectName("Test Project");
//        project.setProjectStartDate(LocalDate.now());
//        project.setProjectEndDate(LocalDate.now().plusDays(30));
//
//
//        Project savedProject = projectRepository.save(project);
//
//        Project existProject = entityManager.find(Project.class, savedProject.getId());
//
//        assertNotNull(existProject);
//        assertEquals(project.getProjectName(), existProject.getProjectName());
//
//    }
//}
