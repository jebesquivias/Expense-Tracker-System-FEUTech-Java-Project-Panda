
package expensetracker;

public enum ExpenseCategory {
    FOOD("Food"),
    TRANSPORTATION("Transportation"),
    UTILITIES("Utilities"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
