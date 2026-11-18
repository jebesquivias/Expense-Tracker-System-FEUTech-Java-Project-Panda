package expensetracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

// Database Persistence mod 7+8
public class ExpenseFileManager {

    // JDBC Variables (Matches Pokedex.java & StudentDatabaseApp.java)
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

    // Update a single exp row
    public static synchronized void updateExpenseInDB(int eid, Expense expense) {
        connectDB();
        if (conn == null) {
            return;
        }
        try {
            String sql = "UPDATE expenses SET edate = ?, ecategory = ?, eamount = ?, edescription = ? WHERE eid = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, expense.getDate().toString());
            pst.setString(2, expense.getCategory().name());
            pst.setDouble(3, expense.getAmount());
            pst.setString(4, expense.getDescription());
            pst.setInt(5, eid);
            pst.executeUpdate();
        } catch (SQLException e1) {
            System.out.println("Database update error: " + e1.getMessage());
        }
    }

    // Save budget and all expenses to database
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

            // Row 0 budget
            pst.setInt(1, 0);
            pst.setString(2, LocalDate.now().toString());
            pst.setString(3, "BUDGET");
            pst.setDouble(4, tracker.getMonthlyBudget());
            pst.setString(5, "Monthly Budget");
            pst.executeUpdate();

            // Rows 1..x records
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

    // Load budget and expenses from dtabase
    public static synchronized void loadFromFile(ExpenseTracker tracker) {
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
                String dateText = rs.getString(2);
                String catText = rs.getString(3);
                double amount = rs.getDouble(4);
                String description = rs.getString(5);

                if (eid == 0 || "BUDGET".equals(catText)) {
                    tracker.setMonthlyBudget(amount);
                } else {
                    LocalDate date = LocalDate.parse(dateText);
                    ExpenseCategory category = ExpenseCategory.valueOf(catText);
                    tracker.addExpense(new Expense(amount, date, description, category));
                }
            }
        } catch (SQLException e1) {
            System.out.println("Database load error: " + e1.getMessage());
        } catch (InvalidExpenseException e2) {
            System.out.println("Invalid data in database: " + e2.getMessage());
        } catch (Exception e3) {
            System.out.println("Error parsing database record: " + e3.getMessage());
        }
    }

    // Save to database mod 7
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
