package expensetracker;

import java.time.LocalDate;

public abstract class Transaction {
    private double amount;
    private LocalDate date;
    private String description;

    public Transaction(double amount, LocalDate date, String description) throws InvalidExpenseException {
        setAmount(amount);
        setDate(date);
        setDescription(description);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) throws InvalidExpenseException {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new InvalidExpenseException("Amount must be a valid number greater than 0.");
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
        this.description = (description == null) ? "" : description.trim().replace("|", "/");
    }

    public abstract String getSummary();
}