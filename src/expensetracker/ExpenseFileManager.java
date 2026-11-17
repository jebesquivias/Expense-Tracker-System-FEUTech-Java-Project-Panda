package expensetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

// Handles Data Persistence using Apache Derby JDBC and Text File Backup (Module 7 & 8)
public class ExpenseFileManager {

    private static final String FILE_NAME = "expenses.txt";

    // JDBC Variables used sir's example of pokedex and login
    static Connection conn;
    static Statement st;
    static PreparedStatement pst;
    static ResultSet rs;
    final static String DB_URL = "jdbc:derby://localhost:1527/expense";
    final static String USERNAME = "java";
    final static String PASSWORD = "java";

    // Establish JDBC Connection to Apache Derby
    public static void connectDB() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.apache.derby.client.ClientAutoloadedDriver");
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e1) {
            System.out.println("Derby Driver not found: " + e1.getMessage());
        } catch (SQLException e2) {
            System.out.println("Database connection offline, using expenses.txt backup.");
        }
    }

    // Save all expenses to the Apache Derby Database Table (Module 8: JDBC)
    public static synchronized void saveToDatabase(ExpenseTracker tracker) {
        connectDB();
        if (conn == null) {
            return;
        }
        try {
            // Clear old rows and insert current list
            st = conn.createStatement();
            st.executeUpdate("DELETE FROM expenses");

            String sql = "INSERT INTO expenses VALUES (?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(sql);

            for (int i = 0; i < tracker.getExpenses().size(); i++) {
                Expense expense = tracker.getExpenses().get(i);
                pst.setInt(1, i + 1);
                pst.setString(2, expense.getDate().toString());
                pst.setString(3, expense.getCategory().name());
                pst.setDouble(4, expense.getAmount());
                pst.setString(5, expense.getDescription());
                pst.executeUpdate();
            }
        } catch (SQLException e1) {
            System.out.println("Database save error: " + e1.getMessage());
        }
    }

    // Load expenses from the Apache Derby Database Table (Module 8: JDBC)
    public static synchronized boolean loadFromDatabase(ExpenseTracker tracker) throws InvalidExpenseException {
        connectDB();
        if (conn == null) {
            return false;
        }
        try {
            String sql = "SELECT * FROM expenses";
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            tracker.getExpenses().clear();
            while (rs.next()) {
                int eid = rs.getInt(1);
                LocalDate date = LocalDate.parse(rs.getString(2));
                ExpenseCategory category = ExpenseCategory.valueOf(rs.getString(3));
                double amount = rs.getDouble(4);
                String description = rs.getString(5);
                tracker.addExpense(new Expense(amount, date, description, category));
            }
            return true;
        } catch (SQLException e1) {
            return false;
        }
    }

    // Save budget and expenses to expenses.txt AND Apache Derby Database
    public static synchronized void saveToFile(ExpenseTracker tracker) throws IOException {
        // 1. Save to Apache Derby Database (Module 8 JDBC)
        saveToDatabase(tracker);

        // 2. Save to expenses.txt (File I/O & Budget Persistence)
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

    // Load budget from file and expenses from Database (or file fallback)
    public static synchronized void loadFromFile(ExpenseTracker tracker) throws IOException, InvalidExpenseException {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            tracker.getExpenses().clear();

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

        // If Apache Derby Database is connected, load the table records from Derby
        loadFromDatabase(tracker);
    }

    // Save using a separate background thread (Module 7: Multithreading)
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
