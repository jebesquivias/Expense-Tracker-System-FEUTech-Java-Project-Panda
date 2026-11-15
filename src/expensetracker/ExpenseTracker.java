package expensetracker;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ExpenseTracker {
    private final List<Expense> expenses = new ArrayList<>();
    private double monthlyBudget = 0.0;

    // --- Input Validation Parsers for NetBeans GUI TextFields ---
    public static double parseAmountInput(String text, String fieldName) throws InvalidExpenseException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidExpenseException(fieldName + " cannot be empty.");
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException ex) {
            throw new InvalidExpenseException(fieldName + " must be a valid numeric amount (no letters or symbols).");
        }
    }

    public static LocalDate parseDateInput(String text) throws InvalidExpenseException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidExpenseException("Date cannot be empty.");
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException ex) {
            throw new InvalidExpenseException("Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    public static YearMonth parseMonthInput(String text) throws InvalidExpenseException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidExpenseException("Month cannot be empty.");
        }
        try {
            return YearMonth.parse(text.trim());
        } catch (DateTimeParseException ex) {
            throw new InvalidExpenseException("Invalid month format. Please use YYYY-MM.");
        }
    }

    // --- Core CRUD Operations ---
    public synchronized void addExpense(Expense expense) {
        if (expense != null) {
            expenses.add(expense);
        }
    }

    public synchronized Expense addExpense(String amountStr, String dateStr, String description, ExpenseCategory category)
            throws InvalidExpenseException {
        Expense expense = new Expense(parseAmountInput(amountStr, "Amount"), parseDateInput(dateStr), description, category);
        expenses.add(expense);
        return expense;
    }

    public synchronized void updateExpense(int index, Expense expense) {
        if (index >= 0 && index < expenses.size() && expense != null) {
            expenses.set(index, expense);
        }
    }

    public synchronized void updateExpense(int index, String amountStr, String dateStr, String description, ExpenseCategory category)
            throws InvalidExpenseException {
        if (index < 0 || index >= expenses.size()) {
            throw new InvalidExpenseException("Please select a valid expense row to update.");
        }
        Expense updated = new Expense(parseAmountInput(amountStr, "Amount"), parseDateInput(dateStr), description, category);
        expenses.set(index, updated);
    }

    public synchronized void deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
        }
    }

    public synchronized List<Expense> getExpenses() {
        return expenses;
    }

    // --- Budget & Balance Calculations ---
    public synchronized void setMonthlyBudget(double budget) throws InvalidExpenseException {
        if (Double.isNaN(budget) || Double.isInfinite(budget) || budget < 0) {
            throw new InvalidExpenseException("Monthly budget cannot be negative.");
        }
        this.monthlyBudget = budget;
    }

    public synchronized void setMonthlyBudget(String budgetStr) throws InvalidExpenseException {
        setMonthlyBudget(parseAmountInput(budgetStr, "Monthly budget"));
    }

    public synchronized double getMonthlyBudget() {
        return monthlyBudget;
    }

    public synchronized double calculateTotal() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    public synchronized double getRemainingBalance() {
        return monthlyBudget - calculateTotal();
    }

    public synchronized boolean isOverBudget() {
        return monthlyBudget > 0 && calculateTotal() > monthlyBudget;
    }

    public synchronized double getMonthlySummary(YearMonth month) {
        double monthlyTotal = 0;
        for (Expense expense : expenses) {
            if (YearMonth.from(expense.getDate()).equals(month)) {
                monthlyTotal += expense.getAmount();
            }
        }
        return monthlyTotal;
    }

    // --- Collections: Filtering, Breakdown, Unique Dates & Sorting ---
    public synchronized List<Expense> filterByCategory(ExpenseCategory category) {
        List<Expense> filtered = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getCategory() == category) {
                filtered.add(expense);
            }
        }
        return filtered;
    }

    public synchronized List<Expense> filterByDate(LocalDate date) {
        List<Expense> filtered = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getDate().equals(date)) {
                filtered.add(expense);
            }
        }
        return filtered;
    }

    public synchronized Map<ExpenseCategory, Double> getBreakdown() {
        Map<ExpenseCategory, Double> breakdown = new EnumMap<>(ExpenseCategory.class);
        for (Expense expense : expenses) {
            ExpenseCategory cat = expense.getCategory();
            breakdown.put(cat, breakdown.getOrDefault(cat, 0.0) + expense.getAmount());
        }
        return breakdown;
    }

    public synchronized Set<LocalDate> getUniqueDates() {
        Set<LocalDate> dates = new TreeSet<>();
        for (Expense expense : expenses) {
            dates.add(expense.getDate());
        }
        return dates;
    }

    public synchronized List<Expense> getExpensesSortedByAmountDesc() {
        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort(Comparator.comparingDouble(Expense::getAmount).reversed());
        return sorted;
    }
}