package com.example.expensetracker.config;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.TransactionRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize categories
        initializeCategories();
        
        // Initialize sample user
        initializeSampleUser();
        
        // Initialize sample transactions
        initializeSampleTransactions();
    }

    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                new Category("Food & Dining", "Restaurants, groceries, and food delivery"),
                new Category("Transportation", "Gas, parking, public transport, car maintenance"),
                new Category("Shopping", "Clothing, electronics, and other purchases"),
                new Category("Entertainment", "Movies, concerts, games, and recreational activities"),
                new Category("Bills & Utilities", "Electricity, water, internet, phone bills"),
                new Category("Healthcare", "Medical expenses, pharmacy, insurance"),
                new Category("Education", "Books, courses, tuition fees"),
                new Category("Travel", "Flights, hotels, vacation expenses"),
                new Category("Salary", "Monthly salary and bonuses"),
                new Category("Freelance", "Freelance work and consulting income"),
                new Category("Investment", "Dividends, interest, and investment returns"),
                new Category("Other Income", "Miscellaneous income sources"),
                new Category("Other Expense", "Miscellaneous expenses")
            );

            categoryRepository.saveAll(categories);
            System.out.println("Sample categories initialized.");
        }
    }

    private void initializeSampleUser() {
        if (userRepository.count() == 0) {
            User sampleUser = new User(
                "johndoe",
                "john.doe@example.com",
                passwordEncoder.encode("password123"),
                "John",
                "Doe"
            );

            userRepository.save(sampleUser);
            System.out.println("Sample user created: username='johndoe', password='password123'");
        }
    }

    private void initializeSampleTransactions() {
        if (transactionRepository.count() == 0) {
            User user = userRepository.findByUsername("johndoe").orElse(null);
            if (user == null) return;

            // Get categories
            Category foodCategory = categoryRepository.findByName("Food & Dining").orElse(null);
            Category transportCategory = categoryRepository.findByName("Transportation").orElse(null);
            Category salaryCategory = categoryRepository.findByName("Salary").orElse(null);
            Category billsCategory = categoryRepository.findByName("Bills & Utilities").orElse(null);
            Category entertainmentCategory = categoryRepository.findByName("Entertainment").orElse(null);

            if (foodCategory == null || transportCategory == null || salaryCategory == null) return;

            List<Transaction> sampleTransactions = Arrays.asList(
                // Income transactions
                new Transaction(Transaction.TransactionType.INCOME, new BigDecimal("5000.00"), 
                    "Monthly Salary", LocalDate.now().minusDays(30), user, salaryCategory),
                new Transaction(Transaction.TransactionType.INCOME, new BigDecimal("1000.00"), 
                    "Freelance Project", LocalDate.now().minusDays(15), user, salaryCategory),

                // Expense transactions
                new Transaction(Transaction.TransactionType.EXPENSE, new BigDecimal("50.00"), 
                    "Grocery Shopping", LocalDate.now().minusDays(2), user, foodCategory),
                new Transaction(Transaction.TransactionType.EXPENSE, new BigDecimal("25.00"), 
                    "Gas Fill-up", LocalDate.now().minusDays(3), user, transportCategory),
                new Transaction(Transaction.TransactionType.EXPENSE, new BigDecimal("120.00"), 
                    "Electricity Bill", LocalDate.now().minusDays(5), user, billsCategory),
                new Transaction(Transaction.TransactionType.EXPENSE, new BigDecimal("30.00"), 
                    "Movie Night", LocalDate.now().minusDays(7), user, entertainmentCategory),
                new Transaction(Transaction.TransactionType.EXPENSE, new BigDecimal("80.00"), 
                    "Restaurant Dinner", LocalDate.now().minusDays(10), user, foodCategory),
                new Transaction(Transaction.TransactionType.EXPENSE, new BigDecimal("15.00"), 
                    "Coffee Shop", LocalDate.now().minusDays(1), user, foodCategory)
            );

            transactionRepository.saveAll(sampleTransactions);
            System.out.println("Sample transactions initialized.");
        }
    }
}