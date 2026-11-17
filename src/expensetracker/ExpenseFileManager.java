package expensetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

// Handles saving and loading expense data to a text file (Module 7 & 8)
public class ExpenseFileManager {
    private static final String FILE_NAME = "expenses.txt";

    // Save budget and all expenses to expenses.txt
    public static synchronized void saveToFile(ExpenseTracker tracker) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
        writer.write("BUDGET," + tracker.getMonthlyBudget());
        writer.newLine();

        for (Expense expense : tracker.getExpenses()) {
            String safeDesc = expense.getDescription().replace(",", " ");
            String line = expense.getDate() + ","
                    + expense.getCategory().name() + ","
                    + expense.getAmount() + ","
                    + safeDesc;
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    // Load budget and expenses from expenses.txt
    public static synchronized void loadFromFile(ExpenseTracker tracker) throws IOException, InvalidExpenseException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        tracker.getExpenses().clear();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("BUDGET,")) {
                String budgetText = line.substring(7);
                tracker.setMonthlyBudget(Double.parseDouble(budgetText));
            } else {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    ExpenseCategory category = ExpenseCategory.valueOf(parts[1]);
                    double amount = Double.parseDouble(parts[2]);
                    String description = parts[3];
                    tracker.addExpense(new Expense(amount, date, description, category));
                }
            }
        }
        reader.close();
    }

    // Save file using a separate background thread (Module 7: Multithreading)
    public static void saveInBackground(final ExpenseTracker tracker) {
        Thread saveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    saveToFile(tracker);
                } catch (IOException e) {
                    System.out.println("Auto-save error: " + e.getMessage());
                }
            }
        });
        saveThread.start();
    }
}
