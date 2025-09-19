package com.example.expensetracker.service;

import com.example.expensetracker.dto.*;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.TransactionRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public TransactionResponse createTransaction(TransactionRequest request) {
        User currentUser = getCurrentUser();
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        Transaction transaction = new Transaction(
                request.getType(),
                request.getAmount(),
                request.getDescription(),
                request.getTransactionDate(),
                currentUser,
                category
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToResponse(savedTransaction);
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        User currentUser = getCurrentUser();
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own transactions");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponse(updatedTransaction);
    }

    public void deleteTransaction(Long id) {
        User currentUser = getCurrentUser();
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own transactions");
        }

        transactionRepository.delete(transaction);
    }

    public TransactionResponse getTransaction(Long id) {
        User currentUser = getCurrentUser();
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view your own transactions");
        }

        return convertToResponse(transaction);
    }

    public Page<TransactionResponse> getTransactions(
            Transaction.TransactionType type,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {
        
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactions = transactionRepository.findTransactionsWithFilters(
                currentUser, type, categoryId, startDate, endDate, pageable);

        return transactions.map(this::convertToResponse);
    }

    public Map<String, Object> getMonthlySummary(int year, int month) {
        User currentUser = getCurrentUser();
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.EXPENSE, startDate, endDate);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        List<Object[]> incomeByCategory = transactionRepository.findCategorySummaryByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.INCOME, startDate, endDate);
        List<Object[]> expenseByCategory = transactionRepository.findCategorySummaryByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.EXPENSE, startDate, endDate);

        Map<String, Object> summary = new HashMap<>();
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netAmount", totalIncome.subtract(totalExpense));
        summary.put("incomeByCategory", convertCategorySummary(incomeByCategory));
        summary.put("expenseByCategory", convertCategorySummary(expenseByCategory));

        return summary;
    }

    public Map<String, Object> getAnnualSummary(int year) {
        User currentUser = getCurrentUser();
        
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.EXPENSE, startDate, endDate);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        List<Object[]> incomeByCategory = transactionRepository.findCategorySummaryByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.INCOME, startDate, endDate);
        List<Object[]> expenseByCategory = transactionRepository.findCategorySummaryByUserAndTypeAndDateBetween(
                currentUser, Transaction.TransactionType.EXPENSE, startDate, endDate);

        Map<String, Object> summary = new HashMap<>();
        summary.put("year", year);
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netAmount", totalIncome.subtract(totalExpense));
        summary.put("incomeByCategory", convertCategorySummary(incomeByCategory));
        summary.put("expenseByCategory", convertCategorySummary(expenseByCategory));

        return summary;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getCategory().getName(),
                transaction.getCategory().getId(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    private List<Map<String, Object>> convertCategorySummary(List<Object[]> categorySummary) {
        return categorySummary.stream()
                .map(row -> {
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("categoryName", row[0]);
                    categoryData.put("amount", row[1]);
                    return categoryData;
                })
                .collect(Collectors.toList());
    }
}