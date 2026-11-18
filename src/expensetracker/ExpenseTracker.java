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

    // Calculate total spending for a specific YearMonth
    public synchronized double getMonthlySummary(YearMonth month) {
        double monthlyTotal = 0;
        for (Expense expense : expenses) {
            if (YearMonth.from(expense.getDate()).equals(month)) {
                monthlyTotal += expense.getAmount();
            }
        }
        return monthlyTotal;
    }

    // Overloaded method accepting String "YYYY-MM"
    public synchronized double getMonthlySummary(String yearMonthPrefix) {
        double monthlyTotal = 0;
        for (Expense expense : expenses) {
            if (expense.getDate().toString().startsWith(yearMonthPrefix)) {
                monthlyTotal += expense.getAmount();
            }
        }
        return monthlyTotal;
    }

    // Filter expenses by category (Module 6: ArrayList)
    public synchronized List<Expense> filterByCategory(ExpenseCategory category) {
        List<Expense> filtered = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getCategory() == category) {
                filtered.add(expense);
            }
        }
        return filtered;
    }

    // Filter expenses by date (Module 6: ArrayList)
    public synchronized List<Expense> filterByDate(LocalDate date) {
        List<Expense> filtered = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getDate().equals(date)) {
                filtered.add(expense);
            }
        }
        return filtered;
    }

    // Get unique dates with recorded expenses (Module 6: HashSet)
    public synchronized Set<LocalDate> getUniqueDates() {
        Set<LocalDate> dates = new HashSet<>();
        for (Expense expense : expenses) {
            dates.add(expense.getDate());
        }
        return dates;
    }

    // Breakdown total spending per category (Module 6: HashMap)
    public synchronized Map<ExpenseCategory, Double> getBreakdown() {
        Map<ExpenseCategory, Double> breakdown = new HashMap<>();
        for (ExpenseCategory cat : ExpenseCategory.values()) {
            breakdown.put(cat, 0.0);
        }
        for (Expense expense : expenses) {
            ExpenseCategory cat = expense.getCategory();
            double currentTotal = breakdown.get(cat);
            breakdown.put(cat, currentTotal + expense.getAmount());
        }
        return breakdown;
    }

    // Sort expenses from highest to lowest amount
    public synchronized List<Expense> getExpensesSortedByAmountDesc() {
        List<Expense> sorted = new ArrayList<>(expenses);
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).getAmount() > sorted.get(i).getAmount()) {
                    Expense temp = sorted.get(i);
                    sorted.set(i, sorted.get(j));
                    sorted.set(j, temp);
                }
            }
        }
        return sorted;
    }
}
