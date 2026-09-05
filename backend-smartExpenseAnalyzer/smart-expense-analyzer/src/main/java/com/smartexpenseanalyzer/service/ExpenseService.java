package com.smartexpenseanalyzer.service;

import com.smartexpenseanalyzer.dto.CategoryResponse;
import com.smartexpenseanalyzer.dto.ExpenseRequest;
import com.smartexpenseanalyzer.dto.ExpenseResponse;
import com.smartexpenseanalyzer.dto.ExpenseSearchRequest;

import com.smartexpenseanalyzer.entity.Category;
import com.smartexpenseanalyzer.entity.Expense;
import com.smartexpenseanalyzer.entity.User;

import com.smartexpenseanalyzer.repository.CategoryRepository;
import com.smartexpenseanalyzer.repository.ExpenseRepository;
import com.smartexpenseanalyzer.repository.ExpenseSpecification;
import com.smartexpenseanalyzer.repository.UserRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {

        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE EXPENSE
    // =========================================================

    public ExpenseResponse createExpense(
            ExpenseRequest request,
            String email) {

        User user = getUser(email);

        Category category = categoryRepository
                .findByIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Expense expense = new Expense();

        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(category);
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setNotes(request.getNotes());
        expense.setUser(user);

        LocalDateTime now = LocalDateTime.now();

        expense.setCreatedAt(now);
        expense.setUpdatedAt(now);

        Expense savedExpense =
                expenseRepository.save(expense);

        return convertToResponse(savedExpense);
    }

    // =========================================================
    // GET ALL EXPENSES
    // =========================================================

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpenses(String email) {

        User user = getUser(email);

        List<Expense> expenses =
                expenseRepository.findByUserOrderByExpenseDateDesc(user);

        return expenses
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET EXPENSE BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public ExpenseResponse getExpense(
            Long id,
            String email) {

        User user = getUser(email);

        Expense expense =
                expenseRepository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException("Expense not found"));

        return convertToResponse(expense);
    }

    // =========================================================
    // SEARCH EXPENSES
    // =========================================================

    @Transactional(readOnly = true)
    public List<ExpenseResponse> searchExpenses(
            ExpenseSearchRequest request,
            String email) {

        User user = getUser(email);

        Specification<Expense> specification =
                Specification.where(
                        ExpenseSpecification.hasUser(user)
                );

        if (request.getKeyword() != null
                && !request.getKeyword().isBlank()) {

            specification = specification.and(
                    ExpenseSpecification.hasKeyword(
                            request.getKeyword()
                    )
            );
        }

        if (request.getCategoryId() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasCategory(
                            request.getCategoryId()
                    )
            );
        }

        if (request.getPaymentMethod() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasPaymentMethod(
                            request.getPaymentMethod()
                    )
            );
        }

        if (request.getFromDate() != null) {

            specification = specification.and(
                    ExpenseSpecification.dateAfterOrEqual(
                            request.getFromDate()
                    )
            );
        }

        if (request.getToDate() != null) {

            specification = specification.and(
                    ExpenseSpecification.dateBeforeOrEqual(
                            request.getToDate()
                    )
            );
        }

        if (request.getMinAmount() != null) {

            specification = specification.and(
                    ExpenseSpecification.amountGreaterThanOrEqual(
                            request.getMinAmount()
                    )
            );
        }

        if (request.getMaxAmount() != null) {

            specification = specification.and(
                    ExpenseSpecification.amountLessThanOrEqual(
                            request.getMaxAmount()
                    )
            );
        }

        // =====================================================
        // SORTING
        // =====================================================

        String sortBy = request.getSortBy();

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "expenseDate";
        }

        if (!sortBy.equals("amount")
                && !sortBy.equals("expenseDate")
                && !sortBy.equals("description")) {

            sortBy = "expenseDate";
        }

        Sort.Direction direction =
                "asc".equalsIgnoreCase(request.getDirection())
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortBy);

        List<Expense> expenses =
                expenseRepository.findAll(
                        specification,
                        sort
                );

        return expenses
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // UPDATE EXPENSE
    // =========================================================

    @Transactional
    public ExpenseResponse updateExpense(
            Long id,
            ExpenseRequest request,
            String email) {

        User user = getUser(email);

        Expense expense =
                expenseRepository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException("Expense not found"));

        Category category =
                categoryRepository
                        .findByIdAndUser(
                                request.getCategoryId(),
                                user
                        )
                        .orElseThrow(() ->
                                new RuntimeException("Category not found"));

        // -----------------------------------------------------
        // Update expense fields
        // -----------------------------------------------------

        expense.setAmount(request.getAmount());

        expense.setDescription(
                request.getDescription()
        );

        expense.setExpenseDate(
                request.getExpenseDate()
        );

        expense.setCategory(category);

        expense.setPaymentMethod(
                request.getPaymentMethod()
        );

        expense.setNotes(
                request.getNotes()
        );

        expense.setUpdatedAt(
                LocalDateTime.now()
        );

        // -----------------------------------------------------
        // Save updated expense
        // -----------------------------------------------------

        expenseRepository.save(expense);

        // -----------------------------------------------------
        // Build response using the already-loaded category
        // -----------------------------------------------------

        ExpenseResponse response =
                new ExpenseResponse();

        response.setId(
                expense.getId()
        );

        response.setAmount(
                expense.getAmount()
        );

        response.setDescription(
                expense.getDescription()
        );

        response.setExpenseDate(
                expense.getExpenseDate()
        );

        response.setPaymentMethod(
                expense.getPaymentMethod() != null
                        ? expense.getPaymentMethod().name()
                        : null
        );

        response.setNotes(
                expense.getNotes()
        );

        // Use the category fetched above
        CategoryResponse categoryResponse =
                new CategoryResponse();

        categoryResponse.setId(
                category.getId()
        );

        categoryResponse.setName(
                category.getName()
        );

        response.setCategory(
                categoryResponse
        );

        return response;
    }

    // =========================================================
    // DELETE EXPENSE
    // =========================================================

    public void deleteExpense(
            Long id,
            String email) {

        User user = getUser(email);

        Expense expense =
                expenseRepository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException("Expense not found"));

        expenseRepository.delete(expense);
    }

    // =========================================================
    // GET USER
    // =========================================================

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // =========================================================
    // CONVERT ENTITY → RESPONSE DTO
    // =========================================================

    private ExpenseResponse convertToResponse(
            Expense expense) {

        ExpenseResponse response =
                new ExpenseResponse();

        response.setId(
                expense.getId()
        );

        response.setAmount(
                expense.getAmount()
        );

        response.setDescription(
                expense.getDescription()
        );

        response.setExpenseDate(
                expense.getExpenseDate()
        );

        response.setPaymentMethod(
                expense.getPaymentMethod() != null
                        ? expense.getPaymentMethod().name()
                        : null
        );

        response.setNotes(
                expense.getNotes()
        );

        if (expense.getCategory() != null) {

            Category category =
                    expense.getCategory();

            CategoryResponse categoryResponse =
                    new CategoryResponse();

            categoryResponse.setId(
                    category.getId()
            );

            categoryResponse.setName(
                    category.getName()
            );

            response.setCategory(
                    categoryResponse
            );
        }

        return response;
    }
}

//package com.smartexpenseanalyzer.service;
//
//import org.springframework.transaction.annotation.Transactional;
//import com.smartexpenseanalyzer.dto.CategoryResponse;
//import com.smartexpenseanalyzer.dto.ExpenseRequest;
//import com.smartexpenseanalyzer.dto.ExpenseResponse;
//import com.smartexpenseanalyzer.dto.ExpenseSearchRequest;
//
//import com.smartexpenseanalyzer.entity.Category;
//import com.smartexpenseanalyzer.entity.Expense;
//import com.smartexpenseanalyzer.entity.User;
//
//import com.smartexpenseanalyzer.repository.CategoryRepository;
//import com.smartexpenseanalyzer.repository.ExpenseRepository;
//import com.smartexpenseanalyzer.repository.ExpenseSpecification;
//import com.smartexpenseanalyzer.repository.UserRepository;
//
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class ExpenseService {
//
//    private final ExpenseRepository expenseRepository;
//    private final CategoryRepository categoryRepository;
//    private final UserRepository userRepository;
//
//    public ExpenseService(
//            ExpenseRepository expenseRepository,
//            CategoryRepository categoryRepository,
//            UserRepository userRepository) {
//
//        this.expenseRepository = expenseRepository;
//        this.categoryRepository = categoryRepository;
//        this.userRepository = userRepository;
//    }
//
//    // =========================================================
//    // CREATE EXPENSE
//    // =========================================================
//
//    public ExpenseResponse createExpense(
//            ExpenseRequest request,
//            String email) {
//
//        User user = getUser(email);
//
//        Category category = categoryRepository
//                .findByIdAndUser(
//                        request.getCategoryId(),
//                        user
//                )
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "Category not found"
//                        )
//                );
//
//        Expense expense = new Expense();
//
//        expense.setAmount(request.getAmount());
//        expense.setDescription(request.getDescription());
//        expense.setExpenseDate(request.getExpenseDate());
//        expense.setCategory(category);
//        expense.setPaymentMethod(request.getPaymentMethod());
//        expense.setNotes(request.getNotes());
//        expense.setUser(user);
//
//        LocalDateTime now = LocalDateTime.now();
//
//        expense.setCreatedAt(now);
//        expense.setUpdatedAt(now);
//
//        Expense savedExpense =
//                expenseRepository.save(expense);
//
//        return convertToResponse(savedExpense);
//    }
//
//    // =========================================================
//    // GET ALL EXPENSES
//    // =========================================================
//
////    @Transactional(readOnly = true)
//    public List<ExpenseResponse> getAllExpenses(String email) {
//
//        User user = getUser(email);
//
//        List<Expense> expenses =
//                expenseRepository.findByUserOrderByExpenseDateDesc(user);
//
//        return expenses.stream()
//                .map(this::convertToResponse)
//                .toList();
//    }
//
//    // =========================================================
//    // GET EXPENSE BY ID
//    // =========================================================
//
//    @Transactional(readOnly = true)
//    public ExpenseResponse getExpense(
//            Long id,
//            String email) {
//
//        User user = getUser(email);
//
//        Expense expense = expenseRepository
//                .findByIdAndUser(id, user)
//                .orElseThrow(() ->
//                        new RuntimeException("Expense not found")
//                );
//
//        return convertToResponse(expense);
//    }
//    
//    // =========================================================
//    // SEARCH / FILTER / SORT EXPENSES
//    // =========================================================
//    
//    @Transactional(readOnly = true)
//    public List<ExpenseResponse> searchExpenses(
//            ExpenseSearchRequest request,
//            String email) {
//
//        User user = getUser(email);
//
//        Specification<Expense> specification =
//                Specification.where(
//                        ExpenseSpecification.hasUser(user)
//                );
//
//        // Keyword
//        if (request.getKeyword() != null
//                && !request.getKeyword().isBlank()) {
//
//            specification = specification.and(
//                    ExpenseSpecification.hasKeyword(
//                            request.getKeyword()
//                    )
//            );
//        }
//
//        // Category
//        if (request.getCategoryId() != null) {
//
//            specification = specification.and(
//                    ExpenseSpecification.hasCategory(
//                            request.getCategoryId()
//                    )
//            );
//        }
//
//        // Payment Method
//        if (request.getPaymentMethod() != null) {
//
//            specification = specification.and(
//                    ExpenseSpecification.hasPaymentMethod(
//                            request.getPaymentMethod()
//                    )
//            );
//        }
//
//        // From Date
//        if (request.getFromDate() != null) {
//
//            specification = specification.and(
//                    ExpenseSpecification.dateAfterOrEqual(
//                            request.getFromDate()
//                    )
//            );
//        }
//
//        // To Date
//        if (request.getToDate() != null) {
//
//            specification = specification.and(
//                    ExpenseSpecification.dateBeforeOrEqual(
//                            request.getToDate()
//                    )
//            );
//        }
//
//        // Minimum Amount
//        if (request.getMinAmount() != null) {
//
//            specification = specification.and(
//                    ExpenseSpecification.amountGreaterThanOrEqual(
//                            request.getMinAmount()
//                    )
//            );
//        }
//
//        // Maximum Amount
//        if (request.getMaxAmount() != null) {
//
//            specification = specification.and(
//                    ExpenseSpecification.amountLessThanOrEqual(
//                            request.getMaxAmount()
//                    )
//            );
//        }
//
//        // =====================================================
//        // SORTING
//        // =====================================================
//
//        String sortBy = request.getSortBy();
//
//        if (sortBy == null || sortBy.isBlank()) {
//            sortBy = "expenseDate";
//        }
//
//        if (!sortBy.equals("amount")
//                && !sortBy.equals("expenseDate")
//                && !sortBy.equals("description")) {
//
//            sortBy = "expenseDate";
//        }
//
//        Sort.Direction direction =
//                "asc".equalsIgnoreCase(
//                        request.getDirection()
//                )
//                        ? Sort.Direction.ASC
//                        : Sort.Direction.DESC;
//
//        Sort sort = Sort.by(direction, sortBy);
//
//        List<Expense> expenses =
//                expenseRepository.findAll(
//                        specification,
//                        sort
//                );
//
//        return expenses.stream()
//                .map(this::convertToResponse)
//                .toList();
//    }
//
//    // =========================================================
//    // UPDATE EXPENSE
//    // =========================================================
//
//    public ExpenseResponse updateExpense(
//            Long id,
//            ExpenseRequest request,
//            String email) {
//
//        User user = getUser(email);
//
//        Expense expense = expenseRepository
//                .findByIdAndUser(id, user)
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "Expense not found"
//                        )
//                );
//
//        Category category = categoryRepository
//                .findByIdAndUser(
//                        request.getCategoryId(),
//                        user
//                )
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "Category not found"
//                        )
//                );
//
//        expense.setAmount(request.getAmount());
//        expense.setDescription(request.getDescription());
//        expense.setExpenseDate(request.getExpenseDate());
//        expense.setCategory(category);
//        expense.setPaymentMethod(request.getPaymentMethod());
//        expense.setNotes(request.getNotes());
//
//        expense.setUpdatedAt(LocalDateTime.now());
//
//        Expense updatedExpense =
//                expenseRepository.save(expense);
//
//        return convertToResponse(updatedExpense);
//    }
//
//    // =========================================================
//    // DELETE EXPENSE
//    // =========================================================
//
//    public void deleteExpense(
//            Long id,
//            String email) {
//
//        User user = getUser(email);
//
//        Expense expense = expenseRepository
//                .findByIdAndUser(id, user)
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "Expense not found"
//                        )
//                );
//
//        expenseRepository.delete(expense);
//    }
//
//    // =========================================================
//    // GET USER
//    // =========================================================
//
//    private User getUser(String email) {
//
//        return userRepository
//                .findByEmail(email)
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "User not found"
//                        )
//                );
//    }
//
//    // =========================================================
//    // ENTITY → RESPONSE DTO
//    // =========================================================
//
//    private ExpenseResponse convertToResponse(
//            Expense expense) {
//
//        ExpenseResponse response =
//                new ExpenseResponse();
//
//        response.setId(expense.getId());
//        response.setAmount(expense.getAmount());
//        response.setDescription(
//                expense.getDescription()
//        );
//        response.setExpenseDate(
//                expense.getExpenseDate()
//        );
//
//        response.setPaymentMethod(
//                expense.getPaymentMethod() != null
//                        ? expense.getPaymentMethod().name()
//                        : null
//        );
//
//        response.setNotes(
//                expense.getNotes()
//        );
//
//        // Convert Category entity to CategoryResponse
//        if (expense.getCategory() != null) {
//
//            Category category =
//                    expense.getCategory();
//
//            CategoryResponse categoryResponse =
//                    new CategoryResponse();
//
//            categoryResponse.setId(
//                    category.getId()
//            );
//
//            categoryResponse.setName(
//                    category.getName()
//            );
//
//            response.setCategory(
//                    categoryResponse
//            );
//        }
//
//        return response;
//    }
//}