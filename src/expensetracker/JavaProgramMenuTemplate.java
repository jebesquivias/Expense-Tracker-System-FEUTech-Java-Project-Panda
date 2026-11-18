package expensetracker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// Main GUI Window for the Expense Tracker System
public class JavaProgramMenuTemplate extends JFrame implements ActionListener {

    // Backend Controller
    ExpenseTracker tracker;
    List<Expense> displayedExpenses;

    // Menu Components (Only File and Help)
    JMenuBar menuBar;
    JMenu fileMenu, helpMenu;
    JMenuItem newItem, openItem, saveItem, exitItem;
    JMenuItem aboutItem;

    // Dashboard Labels
    JLabel budgetLabel, totalLabel, balanceLabel, statusLabel;

    // Table Components
    DefaultTableModel tableModel;
    JTable expenseTable;

    // Bottom Buttons
    JButton budgetButton, addButton, editButton, deleteButton, filterButton;

    // Constructor
    public JavaProgramMenuTemplate() {
        super("Expense Tracker System - Code Panda");
        tracker = new ExpenseTracker();
        displayedExpenses = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));

        // Initialize Menu Bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");

        newItem = new JMenuItem("New Session");
        openItem = new JMenuItem("Open (Load Database)");
        saveItem = new JMenuItem("Save (Save Database)");
        exitItem = new JMenuItem("Exit");
        aboutItem = new JMenuItem("About");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Initialize Top Dashboard Panel
        JPanel dashboardPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        budgetLabel = new JLabel("Monthly Budget: PHP 0.00");
        totalLabel = new JLabel("Total Expenses: PHP 0.00");
        balanceLabel = new JLabel("Remaining Balance: PHP 0.00");
        dashboardPanel.add(budgetLabel);
        dashboardPanel.add(totalLabel);
        dashboardPanel.add(balanceLabel);
        add(dashboardPanel, BorderLayout.NORTH);

        // Initialize Center Table
        String[] columns = {"#", "Date", "Category", "Amount (PHP)", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        // Initialize Bottom Buttons and Status Label
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));

        budgetButton = new JButton("Set Budget");
        addButton = new JButton("Add Expense");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        filterButton = new JButton("Filter / Summary");

        buttonPanel.add(budgetButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(filterButton);

        statusLabel = new JLabel(" Ready.");
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Add Action Listeners for Menu Items
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);
        aboutItem.addActionListener(this);

        // Add Action Listeners for Bottom Buttons
        budgetButton.addActionListener(this);
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        filterButton.addActionListener(this);

        // Load existing database records on startup
        try {
            ExpenseFileManager.loadFromFile(tracker);
            refreshTable(tracker.getExpenses());
        } catch (InvalidExpenseException e1) {
            statusLabel.setText(" Invalid data in database: " + e1.getMessage());
        }
    }

    // Refresh the JTable and Dashboard Labels
    private void refreshTable(List<Expense> listToDisplay) {
        displayedExpenses = new ArrayList<>(listToDisplay);
        tableModel.setRowCount(0);

        for (int i = 0; i < displayedExpenses.size(); i++) {
            Expense expense = displayedExpenses.get(i);
            Object[] row = {
                i + 1,
                expense.getDate(),
                expense.getCategory().getDisplayName(),
                String.format("%.2f", expense.getAmount()),
                expense.getDescription()
            };
            tableModel.addRow(row);
        }

        budgetLabel.setText(String.format("Monthly Budget: PHP %.2f", tracker.getMonthlyBudget()));
        totalLabel.setText(String.format("Total Expenses: PHP %.2f", tracker.calculateTotal()));
        balanceLabel.setText(String.format("Remaining Balance: PHP %.2f", tracker.getRemainingBalance()));
    }

    // Set Monthly Budget Dialog
    private void handleSetBudget() {
        String input = JOptionPane.showInputDialog(this, "Enter Monthly Budget (PHP):", tracker.getMonthlyBudget());
        if (input == null) {
            return;
        }
        try {
            double budget = Double.parseDouble(input.trim());
            tracker.setMonthlyBudget(budget);
            refreshTable(tracker.getExpenses());
            statusLabel.setText(" Monthly budget updated.");
        } catch (NumberFormatException e1) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidExpenseException e2) {
            JOptionPane.showMessageDialog(this, e2.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add or Update Expense Dialog
    private void handleAddOrUpdateExpense(int editIndex) {
        JTextField amountField = new JTextField();
        JComboBox<ExpenseCategory> categoryBox = new JComboBox<>(ExpenseCategory.values());
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField descriptionField = new JTextField();

        String title = "Add New Expense";
        if (editIndex >= 0) {
            Expense existing = tracker.getExpenses().get(editIndex);
            amountField.setText(String.valueOf(existing.getAmount()));
            categoryBox.setSelectedItem(existing.getCategory());
            dateField.setText(existing.getDate().toString());
            descriptionField.setText(existing.getDescription());
            title = "Update Expense #" + (editIndex + 1);
        }

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Amount (PHP):"));
        form.add(amountField);
        form.add(new JLabel("Category:"));
        form.add(categoryBox);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Description (Optional):"));
        form.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                ExpenseCategory category = (ExpenseCategory) categoryBox.getSelectedItem();
                String description = descriptionField.getText();

                Expense expense = new Expense(amount, date, description, category);
                if (editIndex < 0) {
                    tracker.addExpense(expense);
                } else {
                    tracker.updateExpense(editIndex, expense);
                }
                refreshTable(tracker.getExpenses());
                ExpenseFileManager.saveInBackground(tracker);
                statusLabel.setText(" Auto-saved to database.");
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e2) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidExpenseException e3) {
                JOptionPane.showMessageDialog(this, e3.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Find actual index in tracker from selected table row
    private int getSelectedTrackerIndex() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= displayedExpenses.size()) {
            return -1;
        }
        Expense selectedExpense = displayedExpenses.get(selectedRow);
        return tracker.getExpenses().indexOf(selectedExpense);
    }

    // Delete Selected Expense
    private void handleDeleteExpense() {
        int trackerIndex = getSelectedTrackerIndex();
        if (trackerIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense row in the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tracker.deleteExpense(trackerIndex);
            refreshTable(tracker.getExpenses());
            ExpenseFileManager.saveInBackground(tracker);
            statusLabel.setText(" Expense deleted and updated in database.");
        }
    }

    // View and Filter Helper Methods
    private void showAllExpenses() {
        refreshTable(tracker.getExpenses());
        statusLabel.setText(" Showing all expenses.");
    }

    private void filterByCategory() {
        ExpenseCategory selectedCategory = (ExpenseCategory) JOptionPane.showInputDialog(
                this, "Select Category:", "Filter by Category",
                JOptionPane.QUESTION_MESSAGE, null,
                ExpenseCategory.values(), ExpenseCategory.FOOD);
        if (selectedCategory != null) {
            refreshTable(tracker.filterByCategory(selectedCategory));
            statusLabel.setText(" Filtered by category: " + selectedCategory.getDisplayName());
        }
    }

    private void filterByDate() {
        String input = JOptionPane.showInputDialog(this, "Enter date (YYYY-MM-DD):", LocalDate.now());
        if (input == null) {
            return;
        }
        try {
            LocalDate date = LocalDate.parse(input.trim());
            refreshTable(tracker.filterByDate(date));
            statusLabel.setText(" Filtered by date: " + date);
        } catch (DateTimeParseException e1) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortByAmount() {
        refreshTable(tracker.getExpensesSortedByAmountDesc());
        statusLabel.setText(" Sorted from highest to lowest amount.");
    }

    private void showCategoryBreakdown() {
        Map<ExpenseCategory, Double> breakdown = tracker.getBreakdown();
        String message = "Spending Breakdown by Category:\n\n";
        for (ExpenseCategory category : ExpenseCategory.values()) {
            message += String.format("%-18s: PHP %.2f\n", category.getDisplayName(), breakdown.get(category));
        }
        message += String.format("\nTotal Spent: PHP %.2f", tracker.calculateTotal());
        JOptionPane.showMessageDialog(this, message, "Category Breakdown", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMonthlySummary() {
        String input = JOptionPane.showInputDialog(this, "Enter month (YYYY-MM):", YearMonth.now());
        if (input == null) {
            return;
        }
        try {
            YearMonth month = YearMonth.parse(input.trim());
            double total = tracker.getMonthlySummary(month);
            JOptionPane.showMessageDialog(this, String.format("Total expenses for %s: PHP %.2f", month, total), "Monthly Summary", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException e1) {
            JOptionPane.showMessageDialog(this, "Invalid month format. Use YYYY-MM.", "Month Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewFilter() {
        String[] options = {"Show All", "Filter by Category", "Filter by Date", "Sort Highest to Lowest", "Category Breakdown", "Monthly Summary"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a view or summary option:", "View & Filter Expenses",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            showAllExpenses();
        } else if (choice == 1) {
            filterByCategory();
        } else if (choice == 2) {
            filterByDate();
        } else if (choice == 3) {
            sortByAmount();
        } else if (choice == 4) {
            showCategoryBreakdown();
        } else if (choice == 5) {
            showMonthlySummary();
        }
    }

    // Event Handler (ActionListener)
    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == newItem) {
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all current expenses in memory?", "New Session", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tracker.getExpenses().clear();
                refreshTable(tracker.getExpenses());
                statusLabel.setText(" Cleared session.");
            }
        } else if (source == openItem) {
            try {
                ExpenseFileManager.loadFromFile(tracker);
                refreshTable(tracker.getExpenses());
                JOptionPane.showMessageDialog(this, "Expenses loaded from database.", "Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (InvalidExpenseException e1) {
                JOptionPane.showMessageDialog(this, "Validation error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == saveItem) {
            ExpenseFileManager.saveToFile(tracker);
            JOptionPane.showMessageDialog(this, "Expenses saved to database.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } else if (source == budgetButton) {
            handleSetBudget();
        } else if (source == addButton) {
            handleAddOrUpdateExpense(-1);
        } else if (source == editButton) {
            int trackerIndex = getSelectedTrackerIndex();
            if (trackerIndex < 0) {
                JOptionPane.showMessageDialog(this, "Please select an expense row in the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                handleAddOrUpdateExpense(trackerIndex);
            }
        } else if (source == deleteButton) {
            handleDeleteExpense();
        } else if (source == filterButton) {
            handleViewFilter();
        } else if (source == aboutItem) {
            JOptionPane.showMessageDialog(this,
                    "Expense Tracker System\nCreated by Group Code Panda (CS0070L)\nHelps students budget and track daily expenses.",
                    "About", JOptionPane.INFORMATION_MESSAGE);
        } else if (source == exitItem) {
            System.exit(0);
        }
    }

    // Main Method
    public static void main(String[] args) {
        JavaProgramMenuTemplate gui = new JavaProgramMenuTemplate();
        gui.setSize(700, 450);
        gui.setLocationRelativeTo(null);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }
}
