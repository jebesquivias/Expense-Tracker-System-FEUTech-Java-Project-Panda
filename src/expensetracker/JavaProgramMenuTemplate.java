package expensetracker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class JavaProgramMenuTemplate extends JFrame implements ActionListener {
    private final ExpenseTracker tracker = new ExpenseTracker();
    private List<Expense> displayedExpenses = new ArrayList<>();

    private final JMenuItem newItem = new JMenuItem("New Session");
    private final JMenuItem openItem = new JMenuItem("Open (Load expenses.txt)");
    private final JMenuItem saveItem = new JMenuItem("Save (Save expenses.txt)");
    private final JMenuItem exitItem = new JMenuItem("Exit");

    private final JMenuItem budgetItem = new JMenuItem("Set Monthly Budget");
    private final JMenuItem addItem = new JMenuItem("Add Expense");
    private final JMenuItem updateItem = new JMenuItem("Update Selected Expense");
    private final JMenuItem deleteItem = new JMenuItem("Delete Selected Expense");

    private final JMenuItem showAllItem = new JMenuItem("Show All Expenses");
    private final JMenuItem categoryFilterItem = new JMenuItem("Filter by Category");
    private final JMenuItem dateFilterItem = new JMenuItem("Filter by Date");
    private final JMenuItem sortItem = new JMenuItem("Sort by Amount (Highest First)");
    private final JMenuItem breakdownItem = new JMenuItem("Category Breakdown");
    private final JMenuItem monthlySummaryItem = new JMenuItem("Monthly Summary");
    private final JMenuItem aboutItem = new JMenuItem("About");

    private final JLabel budgetLabel = new JLabel("Monthly Budget: PHP 0.00");
    private final JLabel totalLabel = new JLabel("Total Expenses: PHP 0.00");
    private final JLabel balanceLabel = new JLabel("Remaining Balance: PHP 0.00");
    private final JLabel statusLabel = new JLabel(" Ready.");

    private final DefaultTableModel tableModel;
    private final JTable expenseTable;

    private final JButton budgetButton = new JButton("Set Budget");
    private final JButton addButton = new JButton("Add Expense");
    private final JButton editButton = new JButton("Edit Selected");
    private final JButton deleteButton = new JButton("Delete Selected");
    private final JButton filterButton = new JButton("Filter / Summary");

    public JavaProgramMenuTemplate() {
        super("Expense Tracker System - Code Panda");
        setLayout(new BorderLayout(10, 10));

        // Menu Bar Setup
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu manageMenu = new JMenu("Manage");
        JMenu viewMenu = new JMenu("View and Reports");
        JMenu helpMenu = new JMenu("Help");

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

        viewMenu.add(showAllItem);
        viewMenu.add(categoryFilterItem);
        viewMenu.add(dateFilterItem);
        viewMenu.add(sortItem);
        viewMenu.addSeparator();
        viewMenu.add(breakdownItem);
        viewMenu.add(monthlySummaryItem);

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(manageMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Top Dashboard Panel
        JPanel dashboardPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        dashboardPanel.add(budgetLabel);
        dashboardPanel.add(totalLabel);
        dashboardPanel.add(balanceLabel);
        add(dashboardPanel, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"#", "Date", "Category", "Amount (PHP)", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        // Bottom Controls & Status Bar
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        buttonPanel.add(budgetButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(filterButton);

        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Register Listeners
        JMenuItem[] items = {
            newItem, openItem, saveItem, exitItem,
            budgetItem, addItem, updateItem, deleteItem,
            showAllItem, categoryFilterItem, dateFilterItem,
            sortItem, breakdownItem, monthlySummaryItem, aboutItem
        };
        for (JMenuItem item : items) {
            item.addActionListener(this);
        }

        JButton[] buttons = {budgetButton, addButton, editButton, deleteButton, filterButton};
        for (JButton button : buttons) {
            button.addActionListener(this);
        }

        // Load saved budget and expenses on startup
        try {
            ExpenseFileManager.loadFromFile(tracker);
            refreshTable(tracker.getExpenses());
        } catch (IOException | InvalidExpenseException ex) {
            statusLabel.setText(" Could not load previous file: " + ex.getMessage());
        }
    }

    private void refreshTable(List<Expense> listToDisplay) {
        displayedExpenses = new ArrayList<>(listToDisplay);
        tableModel.setRowCount(0);
        for (int i = 0; i < displayedExpenses.size(); i++) {
            tableModel.addRow(displayedExpenses.get(i).toTableRow(i + 1));
        }
        budgetLabel.setText(String.format("Monthly Budget: PHP %.2f", tracker.getMonthlyBudget()));
        totalLabel.setText(String.format("Total Expenses: PHP %.2f", tracker.calculateTotal()));
        balanceLabel.setText(String.format(
            "Remaining Balance: PHP %.2f%s",
            tracker.getRemainingBalance(),
            tracker.isOverBudget() ? " (OVER BUDGET!)" : ""
        ));
    }

    private void triggerBackgroundSave() {
        ExpenseFileManager.saveInBackground(
            tracker,
            () -> SwingUtilities.invokeLater(() -> statusLabel.setText(" Auto-saved to expenses.txt"))
        );
    }

    private void handleSetBudget() {
        String input = JOptionPane.showInputDialog(
            this,
            "Enter Monthly Budget (PHP):",
            String.format("%.2f", tracker.getMonthlyBudget())
        );
        if (input == null) {
            return;
        }
        try {
            tracker.setMonthlyBudget(input);
            refreshTable(tracker.getExpenses());
            triggerBackgroundSave();
            statusLabel.setText(" Monthly budget updated.");
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
        JTextField descriptionField = new JTextField(existing != null ? existing.getDescription() : "");

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Amount (PHP):"));
        form.add(amountField);
        form.add(new JLabel("Category:"));
        form.add(categoryBox);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Description (Optional):"));
        form.add(descriptionField);

        String title = (editIndex == null) ? "Add New Expense" : "Update Expense #" + (editIndex + 1);
        int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                ExpenseCategory category = (ExpenseCategory) categoryBox.getSelectedItem();
                if (editIndex == null) {
                    tracker.addExpense(amountField.getText(), dateField.getText(), descriptionField.getText(), category);
                } else {
                    tracker.updateExpense(editIndex, amountField.getText(), dateField.getText(), descriptionField.getText(), category);
                }
                refreshTable(tracker.getExpenses());
                triggerBackgroundSave();
            } catch (InvalidExpenseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getSelectedTrackerIndex() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= displayedExpenses.size()) {
            return -1;
        }
        return tracker.getExpenses().indexOf(displayedExpenses.get(selectedRow));
    }

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
            triggerBackgroundSave();
        }
    }

    private void showAllExpenses() {
        refreshTable(tracker.getExpenses());
        statusLabel.setText(" Showing all expenses.");
    }

    private void filterByCategory() {
        ExpenseCategory selectedCategory = (ExpenseCategory) JOptionPane.showInputDialog(
            this,
            "Select Category:",
            "Filter by Category",
            JOptionPane.QUESTION_MESSAGE,
            null,
            ExpenseCategory.values(),
            ExpenseCategory.FOOD
        );
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
            LocalDate date = ExpenseTracker.parseDateInput(input);
            refreshTable(tracker.filterByDate(date));
            statusLabel.setText(" Filtered by date: " + date);
        } catch (InvalidExpenseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortByAmount() {
        refreshTable(tracker.getExpensesSortedByAmountDesc());
        statusLabel.setText(" Sorted from highest to lowest amount.");
    }

    private void showCategoryBreakdown() {
        Map<ExpenseCategory, Double> breakdown = tracker.getBreakdown();
        StringBuilder message = new StringBuilder("Spending Breakdown by Category:\n\n");
        for (ExpenseCategory category : ExpenseCategory.values()) {
            message.append(String.format("%-18s: PHP %.2f\n", category.getDisplayName(), breakdown.getOrDefault(category, 0.0)));
        }
        message.append(String.format("\nTotal Spent: PHP %.2f", tracker.calculateTotal()));
        JOptionPane.showMessageDialog(this, message.toString(), "Category Breakdown", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMonthlySummary() {
        String input = JOptionPane.showInputDialog(this, "Enter month (YYYY-MM):", YearMonth.now());
        if (input == null) {
            return;
        }
        try {
            YearMonth month = ExpenseTracker.parseMonthInput(input);
            double total = tracker.getMonthlySummary(month);
            JOptionPane.showMessageDialog(
                this,
                String.format("Total expenses for %s: PHP %.2f", month, total),
                "Monthly Summary",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (InvalidExpenseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Month Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewFilter() {
        String[] options = {
            "Show All",
            "Filter by Category",
            "Filter by Date",
            "Sort Highest to Lowest",
            "Category Breakdown",
            "Monthly Summary"
        };
        int choice = JOptionPane.showOptionDialog(
            this,
            "Choose a view or summary option:",
            "View & Filter Expenses",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
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

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == newItem) {
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all current expenses in memory?", "New Session", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tracker.getExpenses().clear();
                refreshTable(tracker.getExpenses());
                triggerBackgroundSave();
                statusLabel.setText(" Cleared session.");
            }
        } else if (source == openItem) {
            try {
                ExpenseFileManager.loadFromFile(tracker);
                refreshTable(tracker.getExpenses());
                JOptionPane.showMessageDialog(this, "Expenses loaded from expenses.txt.", "Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | InvalidExpenseException ex) {
                JOptionPane.showMessageDialog(this, "Load error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == saveItem) {
            try {
                ExpenseFileManager.saveToFile(tracker);
                JOptionPane.showMessageDialog(this, "Expenses saved to expenses.txt.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == budgetItem || source == budgetButton) {
            handleSetBudget();
        } else if (source == addItem || source == addButton) {
            handleAddOrUpdateExpense(null);
        } else if (source == updateItem || source == editButton) {
            int trackerIndex = getSelectedTrackerIndex();
            if (trackerIndex < 0) {
                JOptionPane.showMessageDialog(this, "Please select an expense row in the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                handleAddOrUpdateExpense(trackerIndex);
            }
        } else if (source == deleteItem || source == deleteButton) {
            handleDeleteExpense();
        } else if (source == filterButton) {
            handleViewFilter();
        } else if (source == showAllItem) {
            showAllExpenses();
        } else if (source == categoryFilterItem) {
            filterByCategory();
        } else if (source == dateFilterItem) {
            filterByDate();
        } else if (source == sortItem) {
            sortByAmount();
        } else if (source == breakdownItem) {
            showCategoryBreakdown();
        } else if (source == monthlySummaryItem) {
            showMonthlySummary();
        } else if (source == aboutItem) {
            JOptionPane.showMessageDialog(
                this,
                "Expense Tracker System\nCreated by Group Code Panda (CS0069L)\nHelps students budget and track daily expenses.",
                "About",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else if (source == exitItem) {
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