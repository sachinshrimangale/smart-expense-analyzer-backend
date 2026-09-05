package com.smartexpenseanalyzer.service;

import com.smartexpenseanalyzer.dto.CategoryRequest;
import com.smartexpenseanalyzer.dto.CategoryResponse;
import com.smartexpenseanalyzer.entity.Category;
import com.smartexpenseanalyzer.entity.User;
import com.smartexpenseanalyzer.repository.CategoryRepository;
import com.smartexpenseanalyzer.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            UserRepository userRepository) {

        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    public List<CategoryResponse> getCategories(String email) {

        User user = getUser(email);

        List<Category> categories =
                categoryRepository.findByUser(user);

        /*
         * If the user does not have any categories,
         * automatically create the default categories.
         */
        if (categories.isEmpty()) {

            createDefaultCategories(user);

            categories =
                    categoryRepository.findByUser(user);
        }

        return categories.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    public CategoryResponse createCategory(
            CategoryRequest request,
            String email) {

        User user = getUser(email);

        if (request == null || request.getName() == null) {
            throw new IllegalArgumentException(
                    "Category name is required"
            );
        }

        String categoryName =
                request.getName().trim();

        if (categoryName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Category name cannot be empty"
            );
        }

        if (categoryRepository.existsByNameAndUser(
                categoryName,
                user)) {

            throw new IllegalArgumentException(
                    "Category already exists"
            );
        }

        Category category = new Category();

        category.setName(categoryName);

        // IMPORTANT:
        // Your Category entity uses setIsDefault()
        category.setIsDefault(false);

        category.setUser(user);
        category.setCreatedAt(java.time.LocalDateTime.now());

        Category savedCategory =
                categoryRepository.save(category);

        return convertToResponse(savedCategory);
    }

    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================

    public CategoryResponse getCategory(
            Long id,
            String email) {

        User user = getUser(email);

        Category category =
                categoryRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found"
                                )
                        );

        return convertToResponse(category);
    }

    // =========================================================
    // UPDATE CATEGORY
    // =========================================================

    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request,
            String email) {

        User user = getUser(email);

        Category category =
                categoryRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found"
                                )
                        );

        // Default categories cannot be modified
        if (Boolean.TRUE.equals(category.getIsDefault())) {

            throw new IllegalArgumentException(
                    "Default categories cannot be modified"
            );
        }

        if (request == null || request.getName() == null) {
            throw new IllegalArgumentException(
                    "Category name is required"
            );
        }

        String categoryName =
                request.getName().trim();

        if (categoryName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Category name cannot be empty"
            );
        }

        if (!category.getName().equalsIgnoreCase(categoryName)
                && categoryRepository.existsByNameAndUser(
                        categoryName,
                        user)) {

            throw new IllegalArgumentException(
                    "Category already exists"
            );
        }

        category.setName(categoryName);

        Category updatedCategory =
                categoryRepository.save(category);

        return convertToResponse(updatedCategory);
    }

    // =========================================================
    // DELETE CATEGORY
    // =========================================================

    public void deleteCategory(
            Long id,
            String email) {

        User user = getUser(email);

        Category category =
                categoryRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found"
                                )
                        );

        // Default categories cannot be deleted
        if (Boolean.TRUE.equals(category.getIsDefault())) {

            throw new IllegalArgumentException(
                    "Default categories cannot be deleted"
            );
        }

        categoryRepository.delete(category);
    }

    // =========================================================
    // CREATE DEFAULT CATEGORIES
    // =========================================================

    private void createDefaultCategories(User user) {

        String[] defaultCategories = {

                "Food",
                "Shopping",
                "Transportation",
                "Entertainment",
                "Bills",
                "Health",
                "Education",
                "Travel",
                "Groceries",
                "Other"
        };

        for (String categoryName : defaultCategories) {

            if (!categoryRepository.existsByNameAndUser(
                    categoryName,
                    user)) {

                Category category = new Category();

                category.setName(categoryName);

                // IMPORTANT
                category.setIsDefault(true);

                category.setUser(user);
                category.setCreatedAt(
                        java.time.LocalDateTime.now()
                );

                categoryRepository.save(category);
            }
        }
    }

    // =========================================================
    // CONVERT ENTITY → RESPONSE
    // =========================================================

    private CategoryResponse convertToResponse(
            Category category) {

        CategoryResponse response =
                new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());

        return response;
    }

    // =========================================================
    // GET USER
    // =========================================================

    private User getUser(String email) {

        return userRepository
                .findByEmail(email.toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }
}