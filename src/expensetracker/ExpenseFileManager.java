package expensetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFileManager {
    private static final String FILE_NAME = "expenses.txt";

    // Saves both Monthly Budget and Expenses list
    public static synchronized void saveToFile(ExpenseTracker tracker) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("BUDGET|" + tracker.getMonthlyBudget());
            writer.newLine();
            for (Expense expense : tracker.getExpenses()) {
                writer.write(
                    expense.getDate() + "|"
                    + expense.getCategory().name() + "|"
                    + expense.getAmount() + "|"
                    + expense.getDescription()
                );
                writer.newLine();
            }
        }
    }

    // Backwards-compatible method for List<Expense>
    public static synchronized void saveToFile(List<Expense> expenses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense expense : expenses) {
                writer.write(
                    expense.getDate() + "|"
                    + expense.getCategory().name() + "|"
                    + expense.getAmount() + "|"
                    + expense.getDescription()
                );
                writer.newLine();
            }
        }
    }

    public static synchronized void loadFromFile(ExpenseTracker tracker) throws IOException, InvalidExpenseException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        tracker.getExpenses().clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("BUDGET|")) {
                    tracker.setMonthlyBudget(Double.parseDouble(line.substring(7)));
                    continue;
                }
                String[] parts = line.split("\\|", 4);
                if (parts.length == 4) {
                    double amount = Double.parseDouble(parts[2]);
                    LocalDate date = LocalDate.parse(parts[0]);
                    ExpenseCategory category = ExpenseCategory.valueOf(parts[1]);
                    String description = parts[3];
                    tracker.addExpense(new Expense(amount, date, description, category));
                }
            }
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            throw new InvalidExpenseException("Corrupted data in expenses.txt: " + ex.getMessage());
        }
    }

    // Background thread save for ExpenseTracker (saves budget + expenses)
    public static void saveInBackground(ExpenseTracker tracker, Runnable onComplete) {
        List<Expense> snapshot = new ArrayList<>(tracker.getExpenses());
        double budgetSnapshot = tracker.getMonthlyBudget();
        Thread saveThread = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write("BUDGET|" + budgetSnapshot);
                writer.newLine();
                for (Expense expense : snapshot) {
                    writer.write(
                        expense.getDate() + "|"
                        + expense.getCategory().name() + "|"
                        + expense.getAmount() + "|"
                        + expense.getDescription()
                    );
                    writer.newLine();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (IOException ex) {
                System.err.println("Background save failed: " + ex.getMessage());
            }
        });
        saveThread.start();
    }

    // Backwards-compatible background save for List<Expense>
    public static void saveInBackground(List<Expense> expenses, Runnable onComplete) {
        List<Expense> snapshot = new ArrayList<>(expenses);
        Thread saveThread = new Thread(() -> {
            try {
                saveToFile(snapshot);
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (IOException ex) {
                System.err.println("Background save failed: " + ex.getMessage());
            }
        });
        saveThread.start();
    }
}