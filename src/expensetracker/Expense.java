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
            throw new InvalidExpenseException("Category cannot be empty.");
        }
        this.category = category;
    }

    public Object[] toTableRow(int rowNumber) {
        return new Object[]{
            rowNumber,
            getDate(),
            category.getDisplayName(),
            String.format("%.2f", getAmount()),
            getDescription()
        };
    }

    @Override
    public String getSummary() {
        return String.format("%s | %s | PHP %.2f | %s", getDate(), category, getAmount(), getDescription());
    }
}