package expensetracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

// Handles Data Persistence using Apache Derby JDBC and Multithreading (Module 7 & 8)
public class ExpenseFileManager {

    // JDBC Variables (Matches Professor's Pokedex.java & Login.java)
    static Connection conn;
    static Statement st;
    static PreparedStatement pst;
    static ResultSet rs;
    final static String DB_URL = "jdbc:derby://localhost:1527/expense";
    final static String USERNAME = "java";
    final static String PASSWORD = "java";

    // Connect to Apache Derby Database
    public static void connectDB() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.apache.derby.client.ClientAutoloadedDriver");
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e1) {
            System.out.println("Driver not found: " + e1.getMessage());
        } catch (SQLException e2) {
            System.out.println("Database connection error: " + e2.getMessage());
        }
    }

    // Save all expenses to the Apache Derby Database Table (Module 8: JDBC)
    public static synchronized void saveToFile(ExpenseTracker tracker) {
        connectDB();
        if (conn == null) {
            return;
        }
        try {
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
    public static synchronized void loadFromFile(ExpenseTracker tracker) throws InvalidExpenseException {
        connectDB();
        if (conn == null) {
            return;
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
        } catch (SQLException e1) {
            System.out.println("Database load error: " + e1.getMessage());
        }
    }

    // Save to database using a separate background thread (Module 7: Multithreading)
    public static void saveInBackground(final ExpenseTracker tracker) {
        Thread saveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                saveToFile(tracker);
            }
        });
        saveThread.start();
    }
}
