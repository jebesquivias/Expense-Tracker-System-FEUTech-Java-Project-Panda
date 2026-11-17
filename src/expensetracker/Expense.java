package expensetracker;

import java.time.LocalDate;

// Child class inheriting from Transaction (Module 4: Inheritance & Polymorphism)
public class Expense extends Transaction {
    private ExpenseCategory category;

    // Constructor
    public Expense(double amount, LocalDate date, String description, ExpenseCategory category) throws InvalidExpenseException {
        super(amount, date, description);
        setCategory(category);
    }

    // Getter
    public ExpenseCategory getCategory() {
        return category;
    }

    // Setter
    public void setCategory(ExpenseCategory category) throws InvalidExpenseException {
        if (category == null) {
            throw new InvalidExpenseException("Please select a valid category.");
        }
        this.category = category;
    }

    @Override
    public String getSummary() {
        return getDate() + " - " + category.getDisplayName() + ": PHP " + String.format("%.2f", getAmount()) + " (" + getDescription() + ")";
    }
}
