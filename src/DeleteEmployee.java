import java.sql.*;
import java.util.Scanner;

public class DeleteEmployee {
    private final EmployeeDatabase employeeDatabase;

    public DeleteEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public void deleteEmployee(Scanner scanner) {
        System.out.print("Enter employee ID to delete: ");
        int empId = scanner.nextInt();
        scanner.nextLine(); 

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
