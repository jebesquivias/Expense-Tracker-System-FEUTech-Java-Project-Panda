package expensetracker;

import java.time.LocalDate;

public class Expense extends Transaction {
    private ExpenseCategory category;

    public Expense(double amount, LocalDate date, String description, ExpenseCategory category) throws InvalidExpenseException {
        super(amount, date, description);
        setCategory(category);
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) throws InvalidExpenseException {
        if (category == null) {
            throw new InvalidExpenseException("Category cannot be null.");
        }
        this.category = category;
    }

    @Override
    public String getSummary() {
        return getDate() + " | " + category + " | PHP " + String.format("%.2f", getAmount()) + " | " + getDescription();
    }
}