package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Category;
import com.demo.project_management_system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryService {

  
    private  final CategoryRepository categoryRepository;


    public void save(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
    }

    public void deleteCategoryById(int id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getAllIssueCategories() {
        return categoryRepository.findAll();
    }
}
