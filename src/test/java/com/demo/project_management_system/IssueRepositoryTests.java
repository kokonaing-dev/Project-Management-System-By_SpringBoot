//package com.demo.project_management_system;
//
//import com.demo.project_management_system.entity.*;
//import com.demo.project_management_system.repository.IssueRepository;
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
//public class IssueRepositoryTests {
//
//    @Autowired
//    private IssueRepository issueRepository;
//
//    @Autowired
//    private ProjectRepository projectRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    public void testCreateIssue(){
//
//        // Create instances of IssueType and Category
//        IssueType issueType = new IssueType();
//        issueType.setIssueName("Task");
//
//        Category category = new Category();
//        category.setCategoryName("RD");
//
//        Project project = new Project();
//        project.setProjectStartDate(LocalDate.now());
//        project.setProjectEndDate(LocalDate.now().plusDays(30));
//        project.setProjectName("PMS Project");
//        project = projectRepository.save(project);
//
//        Issue issue = new Issue();
//        issue.setSubject("Issue Subject");
//        issue.setDescription("Issue Description");
//        issue.setIssueStatus(IssueStatus.OPEN);
//        issue.setPriority(Priority.HIGH);
//        issue.setPlanStartDate(LocalDate.now());
//        issue.setPlanDueDate(LocalDate.now().plusDays(7));
//        issue.setActualStartDate(LocalDate.now());
//        issue.setActualEndDate(LocalDate.now().plusDays(5));
//        issue.setStatus(1); // Example status for soft delete
//        issue.getAttachmentFileNames().add("file1.txt");
//        issue.getAttachmentFileNames().add("file2.txt");
//        issue.setCreatedAt(LocalDate.now());
//
//        issue.setIssueType(issueType); // Set IssueType
//        issue.setCategory(category); // Set Category
//        issue.setProject(project); // Set Project
//
//        // Save the issue using the repository
//        Issue savedIssue = issueRepository.save(issue);
//
//        // Retrieve the issue from the database using EntityManager
//        Issue existIssue = entityManager.find(Issue.class, savedIssue.getId());
//
//        // Assert that the saved issue is not null
//        assertNotNull(existIssue);
//
//        // Assert that the properties of the saved issue match the original issue
//        assertEquals(issue.getSubject(), existIssue.getSubject());
//        assertEquals(issue.getDescription(), existIssue.getDescription());
//        assertEquals(issue.getIssueStatus(), existIssue.getIssueStatus());
//        assertEquals(issue.getPriority(), existIssue.getPriority());
//        assertEquals(issue.getPlanStartDate(), existIssue.getPlanStartDate());
//        assertEquals(issue.getPlanDueDate(), existIssue.getPlanDueDate());
//        assertEquals(issue.getActualStartDate(), existIssue.getActualStartDate());
//        assertEquals(issue.getActualEndDate(), existIssue.getActualEndDate());
//        assertEquals(issue.getStatus(), existIssue.getStatus());
//        assertEquals(issue.getAttachmentFileNames(), existIssue.getAttachmentFileNames());
//        assertEquals(issue.getCreatedAt(), existIssue.getCreatedAt());
//        assertEquals(issue.getIssueType(), existIssue.getIssueType());
//        assertEquals(issue.getCategory(), existIssue.getCategory());
//        assertEquals(issue.getProject(), existIssue.getProject());
//
//    }
//}
