import java.util.Scanner;
import java.sql.*;

/**
 * This class provides methods to update the division of an employee in the database.
 */
public class UpdateEmployeeDivision {
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs an UpdateEmployeeDivision object with a reference to an EmployeeDatabase.
     * @param employeeDatabase The EmployeeDatabase object used to interact with the database.
     */
    public UpdateEmployeeDivision(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Updates the division assignment for an employee. This method allows the user to either update an existing
     * division assignment or assign a new division to an employee if they currently do not have one.
     * The method ensures the division and employee exist before attempting any updates.
     * @param scanner The scanner object to receive input from the user.
     */
    public void updateEmployeeDivision(Scanner scanner) {
        System.out.print("Enter Employee ID: ");
        int empId = scanner.nextInt();
        scanner.nextLine(); 
        if (!employeeExists(empId)) {
            System.out.println("Employee with ID " + empId + " does not exist.");
            return;
        }
        listAvailableDivisions();

        System.out.print("Enter Division ID to assign to employee: ");
        int divisionId = scanner.nextInt();
        scanner.nextLine();

        if (!divisionExists(divisionId)) {
            System.out.println("Division with ID " + divisionId + " does not exist.");
            return;
        }

        int currentDivisionId = getCurrentDivision(empId);
        if (currentDivisionId != -1) {
            if (updateDivision(empId, divisionId)) {
                System.out.println("Employee's division updated successfully.");
            } else {
                System.out.println("Failed to update the employee's division.");
            }
        } else {
            // Assign a new division to the employee
            if (assignNewDivision(empId, divisionId)) {
                System.out.println("Employee's new division assigned successfully.");
            } else {
                System.out.println("Failed to assign a new division to the employee.");
            }
        }
    }

    /**
     * Lists all available divisions from the database, displaying each one's ID and name.
     */
    private void listAvailableDivisions() {
        String query = "SELECT ID, Name FROM division";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                System.out.printf("%d - %s%n", id, name);
            }
        } catch (SQLException e) {
            System.out.println("Error listing divisions: " + e.getMessage());
        }
    }

    /**
     * Checks if a division exists in the database based on the division ID.
     * @param divisionId The division ID to check.
     * @return true if the division exists, false otherwise.
     */
    private boolean divisionExists(int divisionId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM division WHERE ID = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, divisionId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check division existence" );
        }
        return false;
    }

    /**
     * Retrieves the current division ID for an employee.
     * @param empId The employee ID whose division is to be retrieved.
     * @return The division ID if found, -1 otherwise.
     */
    private int getCurrentDivision(int empId) {
        try {
            String query = "SELECT div_ID FROM employee_division WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("div_ID");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get current division for employee");
        }
        return -1;
    }

    /**
     * Updates the division assignment for an existing employee to a new division.
     * Handles database transactions to ensure data integrity.
     * @param empId The employee ID to update.
     * @param divisionId The new division ID to assign.
     * @return true if the update was successful, false otherwise.
     */
    private boolean updateDivision(int empId, int divisionId) {
        boolean success = false;
        try {
            employeeDatabase.connection.setAutoCommit(false);
            // Disable foreign key checks
            try (Statement stmt = employeeDatabase.connection.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=0");
            }
            // Update employee's division
            String query = "UPDATE employee_division SET div_ID = ? WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, divisionId);
                pstmt.setInt(2, empId);
                int affectedRows = pstmt.executeUpdate();
                success = affectedRows > 0;
            }
            // Enable foreign key checks
            try (Statement stmt = employeeDatabase.connection.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
            }
            employeeDatabase.connection.commit();
        } catch (SQLException e) {
            System.out.println("Failed to update employee division: " + e.getMessage());
            try {
                employeeDatabase.connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Failed to rollback: " + ex.getMessage());
            }
        } finally {
            try {
                employeeDatabase.connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
        return success;
    }

    /**
     * Assigns a new division to an employee who currently does not have a division assigned.
     * @param empId The employee ID to assign a new division.
     * @param divisionId The division ID to assign.
     * @return true if the assignment was successful, false otherwise.
     */
    private boolean assignNewDivision(int empId, int divisionId) {
        boolean success = false;
        try {
            String query = "INSERT INTO employee_division (empid, div_ID) VALUES (?, ?)";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                pstmt.setInt(2, divisionId);
                int affectedRows = pstmt.executeUpdate();
                success = affectedRows > 0;
            }
        } catch (SQLException e) {
            System.out.println("Failed to assign new division to employee: " + e.getMessage());
        }
        return success;
    }
    
    /**
     * Checks if an employee exists in the database.
     * @param empId The employee ID to check.
     * @return true if the employee exists, false otherwise.
     */
    private boolean employeeExists(int empId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM employees WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check employee existence: " + e.getMessage());
        }
        return false;
    }
}
