package expensetracker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class JavaProgramMenuTemplate extends JFrame implements ActionListener {

    private final ExpenseTracker tracker;

    // Menu Components
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu manageMenu;
    JMenu helpMenu;

    JMenuItem newItem;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;

    JMenuItem budgetItem;
    JMenuItem addItem;
    JMenuItem updateItem;
    JMenuItem deleteItem;
    JMenuItem viewItem;

    JMenuItem aboutItem;

    // Dashboard & Table Components
    private final JLabel budgetLabel;
    private final JLabel totalLabel;
    private final JLabel balanceLabel;
    private final JLabel statusLabel;
    private final DefaultTableModel tableModel;
    private final JTable expenseTable;

    // Quick-Action Buttons
    private final JButton addBtn;
    private final JButton editBtn;
    private final JButton deleteBtn;
    private final JButton budgetBtn;
    private final JButton filterBtn;

    public JavaProgramMenuTemplate() {
        super("Expense Tracker System - Code Panda");
        this.tracker = new ExpenseTracker();

        setLayout(new BorderLayout(10, 10));

        // 1. Initialize Menu Bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        manageMenu = new JMenu("Manage");
        helpMenu = new JMenu("Help");

        newItem = new JMenuItem("New Session");
        openItem = new JMenuItem("Open (Load expenses.txt)");
        saveItem = new JMenuItem("Save (Save expenses.txt)");
        exitItem = new JMenuItem("Exit");

        budgetItem = new JMenuItem("Set Monthly Budget");
        addItem = new JMenuItem("Add Expense");
        updateItem = new JMenuItem("Update Selected Expense");
        deleteItem = new JMenuItem("Delete Selected Expense");
        viewItem = new JMenuItem("View / Filter / Breakdown");

        aboutItem = new JMenuItem("About");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        manageMenu.add(budgetItem);
        manageMenu.addSeparator();
        manageMenu.add(addItem);
        manageMenu.add(updateItem);
        manageMenu.add(deleteItem);
        manageMenu.add(viewItem);

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(manageMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // 2. Top Dashboard Panel (Budget, Total Spent, Remaining Balance)
        JPanel dashboardPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        budgetLabel = new JLabel("Monthly Budget: PHP 0.00");
        totalLabel = new JLabel("Total Expenses: PHP 0.00");
        balanceLabel = new JLabel("Remaining Balance: PHP 0.00");
        dashboardPanel.add(budgetLabel);
        dashboardPanel.add(totalLabel);
        dashboardPanel.add(balanceLabel);
        add(dashboardPanel, BorderLayout.NORTH);

        // 3. Center Table Panel
        String[] columns = {"#", "Date", "Category", "Amount (PHP)", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        // 4. Bottom Action Bar & Status Panel
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        budgetBtn = new JButton("Set Budget");
        addBtn = new JButton("Add Expense");
        editBtn = new JButton("Edit Selected");
        deleteBtn = new JButton("Delete Selected");
        filterBtn = new JButton("Filter / Summary");

        buttonPanel.add(budgetBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(filterBtn);

        statusLabel = new JLabel(" Ready.");
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // 5. Bind Listeners
        JMenuItem[] items = {newItem, openItem, saveItem, exitItem, budgetItem, addItem, updateItem, deleteItem, viewItem, aboutItem};
        for (JMenuItem item : items) {
            item.addActionListener(this);
        }
        JButton[] buttons = {budgetBtn, addBtn, editBtn, deleteBtn, filterBtn};
        for (JButton btn : buttons) {
            btn.addActionListener(this);
        }

        // Load saved expenses on startup if expenses.txt exists
        try {
            ExpenseFileManager.loadFromFile(tracker);
            refreshTable(tracker.getExpenses());
        } catch (IOException | InvalidExpenseException ex) {
            statusLabel.setText(" Could not load previous file: " + ex.getMessage());
        }
    }

    private void refreshTable(List<Expense> listToDisplay) {
        tableModel.setRowCount(0);
        for (int i = 0; i < listToDisplay.size(); i++) {
            Expense exp = listToDisplay.get(i);
            tableModel.addRow(new Object[]{
                i + 1,
                exp.getDate(),
                exp.getCategory().getDisplayName(),
                String.format("%.2f", exp.getAmount()),
                exp.getDescription()
            });
        }
        budgetLabel.setText(String.format("Monthly Budget: PHP %.2f", tracker.getMonthlyBudget()));
        totalLabel.setText(String.format("Total Expenses: PHP %.2f", tracker.calculateTotal()));
        balanceLabel.setText(String.format("Remaining Balance: PHP %.2f", tracker.getRemainingBalance()));
    }

    private void triggerBackgroundSave() {
        ExpenseFileManager.saveInBackground(tracker.getExpenses(), () -> {
            SwingUtilities.invokeLater(() -> statusLabel.setText(" Auto-saved to expenses.txt"));
        });
    }

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
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidExpenseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddOrUpdateExpense(Integer editIndex) {
        Expense existing = (editIndex != null) ? tracker.getExpenses().get(editIndex) : null;

        JTextField amountField = new JTextField(existing != null ? String.valueOf(existing.getAmount()) : "");
        JComboBox<ExpenseCategory> categoryBox = new JComboBox<>(ExpenseCategory.values());
        if (existing != null) {
            categoryBox.setSelectedItem(existing.getCategory());
        }
        JTextField dateField = new JTextField(existing != null ? existing.getDate().toString() : LocalDate.now().toString());
        JTextField descField = new JTextField(existing != null ? existing.getDescription() : "");

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Amount (PHP):"));
        form.add(amountField);
        form.add(new JLabel("Category:"));
        form.add(categoryBox);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Description (Optional):"));
        form.add(descField);

        String title = (editIndex == null) ? "Add New Expense" : "Update Expense #" + (editIndex + 1);
        int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                if (date.isAfter(LocalDate.now())) {
                    throw new InvalidExpenseException("Expense date cannot be in the future.");
                }
                ExpenseCategory category = (ExpenseCategory) categoryBox.getSelectedItem();
                String desc = descField.getText();

                Expense expense = new Expense(amount, date, desc, category);
                if (editIndex == null) {
                    tracker.addExpense(expense);
                } else {
                    tracker.updateExpense(editIndex, expense);
                }
                refreshTable(tracker.getExpenses());
                triggerBackgroundSave();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidExpenseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense row in the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tracker.deleteExpense(selectedRow);
            refreshTable(tracker.getExpenses());
            triggerBackgroundSave();
        }
    }

    private void handleViewFilter() {
        String[] options = {"Show All", "Filter by Category", "Sort Highest to Lowest", "Category Breakdown"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a view or summary option:", "View & Filter Expenses",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            refreshTable(tracker.getExpenses());
            statusLabel.setText(" Showing all expenses.");
        } else if (choice == 1) {
            ExpenseCategory selectedCat = (ExpenseCategory) JOptionPane.showInputDialog(
                    this, "Select Category:", "Filter by Category",
                    JOptionPane.QUESTION_MESSAGE, null, ExpenseCategory.values(), ExpenseCategory.FOOD);
            if (selectedCat != null) {
                refreshTable(tracker.filterByCategory(selectedCat));
                statusLabel.setText(" Filtered by category: " + selectedCat.getDisplayName());
            }
        } else if (choice == 2) {
            refreshTable(tracker.getExpensesSortedByAmountDesc());
            statusLabel.setText(" Sorted from highest to lowest amount.");
        } else if (choice == 3) {
            Map<ExpenseCategory, Double> breakdown = tracker.getBreakdown();
            StringBuilder sb = new StringBuilder("Spending Breakdown by Category:\n\n");
            for (ExpenseCategory cat : ExpenseCategory.values()) {
                sb.append(String.format("%-18s: PHP %.2f\n", cat.getDisplayName(), breakdown.getOrDefault(cat, 0.0)));
            }
            sb.append(String.format("\nTotal Spent: PHP %.2f", tracker.calculateTotal()));
            JOptionPane.showMessageDialog(this, sb.toString(), "Category Breakdown", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == newItem) {
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all current expenses in memory?", "New Session", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tracker.getExpenses().clear();
                refreshTable(tracker.getExpenses());
                statusLabel.setText(" Cleared session.");
            }
        } else if (src == openItem) {
            try {
                ExpenseFileManager.loadFromFile(tracker);
                refreshTable(tracker.getExpenses());
                JOptionPane.showMessageDialog(this, "Expenses loaded from expenses.txt.", "Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | InvalidExpenseException ex) {
                JOptionPane.showMessageDialog(this, "Load error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (src == saveItem) {
            try {
                ExpenseFileManager.saveToFile(tracker.getExpenses());
                JOptionPane.showMessageDialog(this, "Expenses saved to expenses.txt.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (src == budgetItem || src == budgetBtn) {
            handleSetBudget();
        } else if (src == addItem || src == addBtn) {
            handleAddOrUpdateExpense(null);
        } else if (src == updateItem || src == editBtn) {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an expense row in the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                handleAddOrUpdateExpense(selectedRow);
            }
        } else if (src == deleteItem || src == deleteBtn) {
            handleDeleteExpense();
        } else if (src == viewItem || src == filterBtn) {
            handleViewFilter();
        } else if (src == aboutItem) {
            JOptionPane.showMessageDialog(this,
                    "Expense Tracker System\nCreated by Group Code Panda (CS0069L)\nHelps students budget and track daily expenses.",
                    "About", JOptionPane.INFORMATION_MESSAGE);
        } else if (src == exitItem) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JavaProgramMenuTemplate gui = new JavaProgramMenuTemplate();
            gui.setSize(700, 450);
            gui.setLocationRelativeTo(null);
            gui.setResizable(true);
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.setVisible(true);
        });
    }
}