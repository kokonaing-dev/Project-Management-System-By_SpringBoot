package com.demo.project_management_system.controller.api;

import com.demo.project_management_system.entity.Category;
import com.demo.project_management_system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class IssueCategoryController {

    @Autowired
    private CategoryService issueCategoryService;

    @GetMapping("/api/issue-categories")
    public ResponseEntity<List<Category>> getAllIssueCategories() {
        List<Category> issueCategories = issueCategoryService.getAllIssueCategories();
        return ResponseEntity.ok(issueCategories);
    }

    @PostMapping("/saveIssueCategories")
    public String saveIssueCategories(@ModelAttribute("issueCategories") Category category) {
        issueCategoryService.save(category);
        return "redirect:/setting"; // Redirect to the page where you display issue categories
    }

    @DeleteMapping("/deleteCategory/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryId") int categoryId) {
        try {
            issueCategoryService.deleteCategoryById(categoryId);
            return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
