package expensetracker;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Controller class managing the list of expenses and budget calculations (Module 2, 4, 6 & 7)
public class ExpenseTracker {
    private List<Expense> expenses;
    private double monthlyBudget;

    // Constructor
    public ExpenseTracker() {
        this.expenses = new ArrayList<>();
        this.monthlyBudget = 0.0;
    }

    // Add a new expense
    public synchronized void addExpense(Expense expense) {
        if (expense != null) {
            expenses.add(expense);
        }
    }

    // Update an existing expense by index
    public synchronized void updateExpense(int index, Expense expense) {
        if (index >= 0 && index < expenses.size() && expense != null) {
            expenses.set(index, expense);
        }
    }

    // Delete an expense by index
    public synchronized void deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
        }
    }

    public synchronized List<Expense> getExpenses() {
        return expenses;
    }

    // Budget getters and setters
    public synchronized void setMonthlyBudget(double budget) throws InvalidExpenseException {
        if (budget < 0) {
            throw new InvalidExpenseException("Monthly budget cannot be negative.");
        }
        this.monthlyBudget = budget;
    }

    public synchronized double getMonthlyBudget() {
        return monthlyBudget;
    }

    // Calculate total expenses
    public synchronized double calculateTotal() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    // Calculate remaining budget balance
    public synchronized double getRemainingBalance() {
        return monthlyBudget - calculateTotal();
    }

    // Calculate total spending for a specific YearMonth (Module 2 & 4: Method Overloading)
    public synchronized double getMonthlySummary(YearMonth month) {
        double monthlyTotal = 0;
        for (Expense expense : expenses) {
            if (YearMonth.from(expense.getDate()).equals(month)) {
                monthlyTotal += expense.getAmount();
            }
        }
        return monthlyTotal;
    }

    // Overloaded method accepting String "YYYY-MM" (Module 4: Compile-Time Polymorphism)
    public synchronized double getMonthlySummary(String yearMonthPrefix) {
        double monthlyTotal = 0;
        for (Expense expense : expenses) {
            if (expense.getDate().toString().startsWith(yearMonthPrefix)) {
                monthlyTotal += expense.get
