//package com.demo.project_management_system;
//
//import com.demo.project_management_system.entity.Category;
//import com.demo.project_management_system.entity.Issue;
//import com.demo.project_management_system.repository.CategoryRepository;
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
//public class CategoryRepositoryTests {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    public void testCreateCategory(){
//        Category category = new Category();
//        category.setCategoryName("Basic Design");
//
//        Category savedCategory = categoryRepository.save(category);
//
//        Category existCategory = entityManager.find(Category.class, savedCategory.getId());
//
//        assertNotNull(existCategory);
//        assertEquals(category.getCategoryName(), existCategory.getCategoryName());
//    }
//}
