package expensetracker;

import java.time.LocalDate;

// Abstract parent class representing a general financial transaction (Module 3 & 4)
public abstract class Transaction {
    private double amount;
    private LocalDate date;
    private String description;

    // Constructor
    public Transaction(double amount, LocalDate date, String description) throws InvalidExpenseException {
        setAmount(amount);
        setDate(date);
        setDescription(description);
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) throws InvalidExpenseException {
        if (amount <= 0) {
            throw new InvalidExpenseException("Amount must be greater than 0.");
        }
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) throws InvalidExpenseException {
        if (date == null) {
            throw new InvalidExpenseException("Date cannot be empty.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new InvalidExpenseException("Expense date cannot be in the future.");
        }
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            this.description = "";
        } else {
            this.description = description.trim();
        }
    }

    // Abstract method to be overridden by child classes
    public abstract String getSummary();
}
