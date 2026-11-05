package expensetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFileManager {
    private static final String FILE_NAME = "expenses.txt";

    public static synchronized void saveToFile(List<Expense> expenses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                String line = e.getDate() + "|" + e.getCategory().name() + "|"
                        + e.getAmount() + "|" + e.getDescription();
                writer.write(line);
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
                String[] parts = line.split("\\|", 4);
                if (parts.length == 4) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    ExpenseCategory category = ExpenseCategory.valueOf(parts[1]);
                    double amount = Double.parseDouble(parts[2]);
                    String desc = parts[3];
                    tracker.addExpense(new Expense(amount, date, desc, category));
                }
            }
        }
    }

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