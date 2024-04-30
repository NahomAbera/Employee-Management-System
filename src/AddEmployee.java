import java.sql.*;
import java.util.Scanner;

/**
 * The AddEmployee class handles the addition of new employees to the database.
 * It interacts with the database to insert new employee records, ensuring the uniqueness
 * of employee IDs and, if applicable, Social Security Numbers (SSNs).
 */
public class AddEmployee {
    /**
     * An instance of EmployeeDatabase to manage database connections and operations.
     */
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs an AddEmployee object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    public AddEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Prompts the user to enter details for a new employee and adds them to the database.
     * Ensures that employee IDs and SSNs (if applicable) do not already exist in the database
     * to maintain uniqueness.
     * 
     * @param scanner A Scanner instance for reading user input from the console.
     */
    public void addEmployee(Scanner scanner) {
        System.out.println("Enter employee details:");
        System.out.print("Employee ID: ");
        int empId = scanner.nextInt();
        scanner.nextLine();

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

        boolean ssnExists = checkSSNColumnExists();
        String ssn = null;
        if (ssnExists) {
            System.out.print("SSN (no dashes): ");
            ssn = scanner.nextLine();
            if (ssnAlreadyExists(ssn)) {
                System.out.println("Employee with SSN " + ssn + " already exists.");
                return;
            }
        }

        System.out.print("Address(123 Main Street, City, ST): ");
        String address = scanner.nextLine();
        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();

        insertEmployee(empId, firstName, lastName, email, hireDate, salary, ssn, address, dob, ssnExists);
    }

    /**
     * Checks if the SSN column exists in the employees table of the database.
     * 
     * @return true if the SSN column exists, false otherwise.
     */
    private boolean checkSSNColumnExists() {
        try (ResultSet resultSet = employeeDatabase.connection.getMetaData().getColumns(null, null, "employees", "SSN")) {
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Failed to check SSN column existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an SSN already exists in the database to ensure uniqueness.
     * 
     * @param ssn The SSN to check.
     * @return true if the SSN already exists, false otherwise.
     */
    private boolean ssnAlreadyExists(String ssn) {
        String query = "SELECT COUNT(*) AS count FROM employees WHERE SSN = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setString(1, ssn);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Failed to check SSN existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an employee ID already exists in the database.
     * 
     * @param empId The employee ID to check.
     * @return true if the employee ID already exists, false otherwise.
     */
    private boolean employeeExists(int empId) {
        String query = "SELECT COUNT(*) AS count FROM employees WHERE empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Failed to check employee existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new employee record into the database using the provided details.
     * 
     * @param empId The employee ID.
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @param email The email address of the employee.
     * @param hireDate The hire date of the employee.
     * @param salary The salary of the employee.
     * @param ssn The SSN of the employee (may be null if SSN column doesn't exist).
     * @param address The address of the employee.
     * @param dob The date of birth of the employee.
     * @param ssnExists Indicates if the SSN column exists in the database.
     */
    private void insertEmployee(int empId, String firstName, String lastName, String email, String hireDate, double salary, String ssn, String address, String dob, boolean ssnExists) {
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
}
