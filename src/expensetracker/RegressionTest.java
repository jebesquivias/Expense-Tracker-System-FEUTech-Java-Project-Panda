package expensetracker;

import java.time.LocalDate;

public class RegressionTest {
    public static void main(String[] args) throws Exception {
        ExpenseTracker tracker = new ExpenseTracker();
        tracker.setMonthlyBudget("1000.00");

        // 1. Test valid additions & math
        tracker.addExpense("250.00", LocalDate.now().toString(), "Lunch", ExpenseCategory.FOOD);
        tracker.addExpense("150.00", LocalDate.now().toString(), "Jeepney", ExpenseCategory.TRANSPORTATION);

        if (tracker.calculateTotal() != 400.0) {
            throw new AssertionError("Regression Failure: calculateTotal() expected 400.0 but got " + tracker.calculateTotal());
        }
        if (tracker.getRemainingBalance() != 600.0) {
            throw new AssertionError("Regression Failure: getRemainingBalance() expected 600.0 but got " + tracker.getRemainingBalance());
        }

        // 2. Test rejection of negative amounts, zero, non-numeric text, and future dates
        String[][] invalidSamples = {
            {"-50", LocalDate.now().toString()},
            {"0", LocalDate.now().toString()},
            {"abc", LocalDate.now().toString()},
            {"100", LocalDate.now().plusDays(1).toString()}
        };

        for (String[] sample : invalidSamples) {
            try {
                tracker.addExpense(sample[0], sample[1], "Invalid Test", ExpenseCategory.OTHER);
                throw new AssertionError("Validation failed to reject invalid input: " + sample[0] + " / " + sample[1]);
            } catch (InvalidExpenseException expected) {
                // Expected exception thrown
            }
        }

        System.out.println("ALL REGRESSION TESTS PASSED!");
    }
}