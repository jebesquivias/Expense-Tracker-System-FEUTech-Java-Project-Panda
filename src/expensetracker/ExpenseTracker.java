package expensetracker;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ExpenseTracker {
    private final List<Expense> expenses;
    private double monthlyBudget = 0.0;

    public ExpenseTracker() {
        this.expenses = new ArrayList<>();
    }

    public synchronized void addExpense(Expense expense) {
        if (expense != null) {
            expenses.add(expense);
        }
    }

    public synchronized void updateExpense(int index, Expense expense) {
        if (index >= 0 && index < expenses.size() && expense != null) {
            expenses.set(index, expense);
        }
    }

    public synchronized void deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
        }
    }

    public synchronized List<Expense> getExpenses() {
        return expenses;
    }

    public synchronized double calculateTotal() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
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

    public synchronized List<Expense> filterByCategory(ExpenseCategory category) {
        List<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getCategory() == category) {
                result.add(expense);
            }
        }
        return result;
    }

    public synchronized List<Expense> filterByDate(LocalDate date) {
        List<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getDate().equals(date)) {
                result.add(expense);
            }
        }
        return result;
    }

    public synchronized Map<ExpenseCategory, Double> getBreakdown() {
        Map<ExpenseCategory, Double> breakdown = new EnumMap<>(ExpenseCategory.class);
        for (Expense expense : expenses) {
            ExpenseCategory cat = expense.getCategory();
            breakdown.put(cat, breakdown.getOrDefault(cat, 0.0) + expense.getAmount());
        }
        return breakdown;
    }

    public synchronized void setMonthlyBudget(double budget) throws InvalidExpenseException {
        if (budget < 0) {
            throw new InvalidExpenseException("Budget cannot be negative.");
        }
        this.monthlyBudget = budget;
    }

    public synchronized double getMonthlyBudget() {
        return monthlyBudget;
    }

    public synchronized double getRemainingBalance() {
        return monthlyBudget - calculateTotal();
    }

    public synchronized Set<LocalDate> getUniqueDates() {
        Set<LocalDate> dates = new TreeSet<>();
        for (Expense e : expenses) {
            dates.add(e.getDate());
        }
        return dates;
    }

    public synchronized List<Expense> getExpensesSortedByAmountDesc() {
        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort(Comparator.comparingDouble(Expense::getAmount).reversed());
        return sorted;
    }
}