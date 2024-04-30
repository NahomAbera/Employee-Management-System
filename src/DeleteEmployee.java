import java.sql.*;
import java.util.Scanner;

/**
 * The DeleteEmployee class handles the deletion of employees from the database.
 * It provides a method to delete an employee record based on the employee ID.
 */
public class DeleteEmployee {
    /**
     * An instance of EmployeeDatabase to manage database connections and operations.
     */
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs a DeleteEmployee object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    public DeleteEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Deletes an employee from the database based on the employee ID provided by the user.
     * If the employee exists and is deleted, a success message is displayed.
     * If the employee does not exist, an error message is shown.
     * 
     * @param scanner A Scanner instance for reading the employee ID input from the console.
     */
    public void deleteEmployee(Scanner scanner) {
        System.out.print("Enter employee ID to delete: ");
        int empId = scanner.nextInt();
        scanner.nextLine();  // Consume newline left-over

        try {
            String query = "DELETE FROM employees WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Employee deleted successfully.");
                } else {
                    System.out.println("Employee with ID " + empId + " not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete employee: " + e.getMessage());
        }
    }
}