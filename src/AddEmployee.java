import java.sql.*;
import java.util.Scanner;

public class AddEmployee {
    private final EmployeeDatabase employeeDatabase;

    public AddEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public void addEmployee(Scanner scanner) {
        System.out.println("Enter employee details:");
        System.out.print("Employee ID: ");
        int empId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Hire Date (YYYY-MM-DD): ");
        String hireDate = scanner.nextLine();
        System.out.print("Salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine(); 
        
        // Check if SSN column exists before asking for SSN
        boolean ssnExists = checkSSNColumnExists();
        String ssn = null;
        if (ssnExists) {
            System.out.print("SSN (no dashes): ");
            ssn = scanner.nextLine();
        }

        String query;
        if (ssnExists) {
            query = "INSERT INTO employees (empid, Fname, Lname, email, HireDate, Salary, SSN) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO employees (empid, Fname, Lname, email, HireDate, Salary) VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setDate(5, Date.valueOf(hireDate));
            pstmt.setDouble(6, salary);
            if (ssnExists) {
                pstmt.setString(7, ssn);
            }
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully.");
            } else {
                System.out.println("Failed to add employee. No rows affected.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add employee: " + e.getMessage());
        }
    }

    private boolean checkSSNColumnExists() {
        try {
            DatabaseMetaData metaData = employeeDatabase.connection.getMetaData();
            try (ResultSet resultSet = metaData.getColumns(null, null, "employees", "SSN")) {
                return resultSet.next(); // Returns true if the SSN column exists
            }
        } catch (SQLException e) {
            System.out.println("Failed to check SSN column existence: " + e.getMessage());
            return false;
        }
    }
}
