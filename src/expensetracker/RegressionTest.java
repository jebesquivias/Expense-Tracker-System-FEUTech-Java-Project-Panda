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
        System.out.println("ALL REGRESSION TESTS PASSED!");
    }
}
