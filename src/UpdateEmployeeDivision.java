import java.util.Scanner;
import java.sql.*;

public class UpdateEmployeeDivision {
    private final EmployeeDatabase employeeDatabase;

    public UpdateEmployeeDivision(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

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
