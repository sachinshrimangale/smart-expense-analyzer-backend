package com.smartexpenseanalyzer.controller;

import com.smartexpenseanalyzer.dto.CategoryRequest;
import com.smartexpenseanalyzer.dto.CategoryResponse;
import com.smartexpenseanalyzer.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                categoryService.createCategory(
                        request,
                        email
                )
        );
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                categoryService.getCategories(email)
        );
    }

    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                categoryService.getCategory(
                        id,
                        email
                )
        );
    }

    // =========================================================
    // UPDATE CATEGORY
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                categoryService.updateCategory(
                        id,
                        request,
                        email
                )
        );
    }

    // =========================================================
    // DELETE CATEGORY
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        categoryService.deleteCategory(
                id,
                email
        );

        return ResponseEntity.ok(
                "Category deleted successfully"
        );
    }
}