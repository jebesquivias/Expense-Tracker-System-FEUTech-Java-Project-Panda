package expensetracker;

import java.time.LocalDate;

public class RegressionTest {
    public static void main(String[] args) throws Exception {
        ExpenseTracker tracker = new ExpenseTracker();
        tracker.setMonthlyBudget(1000.0);

 
        tracker.addExpense(new Expense(250.0, LocalDate.now(), "Lunch", ExpenseCategory.FOOD));
        tracker.addExpense(new Expense(150.0, LocalDate.now(), "Jeepney", ExpenseCategory.TRANSPORTATION));

        if (tracker.calculateTotal() != 400.0) {
            throw new AssertionError("Regression Failure: calculateTotal() expected 400.0 but got " + tracker.calculateTotal());
        }
        if (tracker.getRemainingBalance() != 600.0) {
            throw new AssertionError("Regression Failure: getRemainingBalance() expected 600.0 but got " + tracker.getRemainingBalance());
        }

     
        boolean caughtNegative = false;
        try {
            new Expense(-50.0, LocalDate.now(), "Invalid", ExpenseCategory.OTHER);
        } catch (InvalidExpenseException e) {
            caughtNegative = true;
        }
        if (!caughtNegative) {
            throw new AssertionError("Regression Failure: Negative expense amount was not rejected!");
        }

        System.out.println("ALL REGRESSION TESTS PASSED!");
    }
}