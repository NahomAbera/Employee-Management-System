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

        // Check if empid already exists
        if (employeeExists(empId)) {
            System.out.println("Employee with ID " + empId + " already exists.");
            return;
        }

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

        // Check if SSN column exists
        boolean ssnExists = checkSSNColumnExists();
        String ssn = null;
        if (ssnExists) {
            System.out.print("SSN (no dashes): ");
            ssn = scanner.nextLine();

            // Check uniqueness of SSN
            if (ssnAlreadyExists(ssn)) {
                System.out.println("Employee with SSN " + ssn + " already exists.");
                return;
            }
        }
        
        System.out.print("Address(123 Main Street, City, ST): ");
        String address = scanner.nextLine();
        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();

        String query;
        if (ssnExists) {
            query = "INSERT INTO employees (empid, Fname, Lname, email, HireDate, Salary, SSN, address, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO employees (empid, Fname, Lname, email, HireDate, Salary, address, date_Of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
                pstmt.setString(8, address);
                pstmt.setDate(9, Date.valueOf(dob));
            } else {
                pstmt.setString(7, address);
                pstmt.setDate(8, Date.valueOf(dob));
            }
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully.");
            } else {
                System.out.println("Failed to add employee.");
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

    private boolean ssnAlreadyExists(String ssn) {
        try {
            String query = "SELECT COUNT(*) AS count FROM employees WHERE SSN = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setString(1, ssn);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check SSN existence: " + e.getMessage());
        }
        return false;
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
