import java.sql.*;
import java.util.Scanner;

public class SearchEmployee {
    private final EmployeeDatabase employeeDatabase;

    public SearchEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public void searchEmployee(Scanner scanner) {
        System.out.print("Enter employee first name, last name, or Employee ID to search: ");
        String searchInput = scanner.nextLine();
    
        try {
            int empId = Integer.parseInt(searchInput);
            String query = "SELECT * FROM employees WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                executeSearch(pstmt);
            }
        } catch (NumberFormatException e) {
            String query = "SELECT * FROM employees WHERE Fname LIKE ? OR Lname LIKE ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setString(1, "%" + searchInput + "%");
                pstmt.setString(2, "%" + searchInput + "%");
                executeSearch(pstmt);
            } catch (SQLException ex) {
                System.out.println("Failed to search for employee: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Failed to search for employee: " + ex.getMessage());
        }
    }
    

    void executeSearch(PreparedStatement pstmt) throws SQLException {
        try (ResultSet resultSet = employeeDatabase.executeQuery(pstmt)) {
            boolean found = false;
            while (resultSet.next()) {
                found = true;
                int empId = resultSet.getInt("empid");
                String firstName = resultSet.getString("Fname");
                String lastName = resultSet.getString("Lname");
                String email = resultSet.getString("email");
                Date hireDate = resultSet.getDate("HireDate");
                double salary = resultSet.getDouble("Salary");
                String ssn = resultSet.getString("SSN");
    
                System.out.println("\nEmployee ID: " + empId);
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName);
                System.out.println("Email: " + email);
                System.out.println("Hire Date: " + hireDate);
                System.out.println("Salary: " + salary);
                if (isSSNColumnAvailable()) {
                    System.out.println("SSN: " + ssn);
                } else {
                    System.out.println("SSN column is not available.");
                }
            }
            if (!found) {
                System.out.println("No employee found matching the search criteria.");
            }
        }
    }

    boolean isSSNColumnAvailable() {
        try {
            DatabaseMetaData metaData = employeeDatabase.connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "employees", "SSN");
            return columns.next(); // If next() returns true column exists, otherwise it doesn't exist.
        } catch (SQLException e) {
            System.out.println("Failed to check SSN column availability: " + e.getMessage());
            return false;
        }
    }
}
