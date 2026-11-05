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
        if (amount <= 0) {
            throw new InvalidExpenseException("Amount must be greater than 0.");
        }
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date != null && !date.isAfter(LocalDate.now())) {
            this.date = date;
        } else {
            this.date = LocalDate.now();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    public abstract String getSummary();
}